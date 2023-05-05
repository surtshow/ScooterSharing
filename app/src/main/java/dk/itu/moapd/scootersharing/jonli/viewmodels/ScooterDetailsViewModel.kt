package dk.itu.moapd.scootersharing.jonli.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.StorageReference
import dk.itu.moapd.scootersharing.jonli.enumerators.RideStatus
import dk.itu.moapd.scootersharing.jonli.interfaces.OnGetDataListener
import dk.itu.moapd.scootersharing.jonli.models.Ride
import dk.itu.moapd.scootersharing.jonli.models.Scooter
import dk.itu.moapd.scootersharing.jonli.utils.Utils.calculatePrice
import kotlinx.coroutines.launch

class ScooterDetailsViewModel(
    private val scooterId: String,
    private var rideId: String?,
) : LocationViewModel() {

    var scooter = MutableLiveData<Scooter?>(null)
    var ride = MutableLiveData<Ride?>(null)

    init {
        viewModelScope.launch {
            getScooter()
            getRide()
        }
    }

    private fun getScooter() {
        auth.currentUser?.let {
            database.child("scooters")
                .child(scooterId)
                .get()
                .addOnSuccessListener { snapshot ->
                    snapshot.getValue(Scooter::class.java)?.let { scooter ->
                        this.scooter.value = scooter
                    }
                }
        }
    }

    private fun getRide() {
        rideId?.let { rideId ->
            auth.currentUser?.let {
                database.child("rides")
                    .child(it.uid)
                    .child(rideId)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        snapshot.getValue(Ride::class.java)?.let { ride ->
                            this.ride.value = ride
                        }
                    }
            }
        }
    }

    fun getScooterImage(): StorageReference {
        scooter.value?.let {
            it.image?.let { image ->
                return storage.reference.child(image)
            }
        }
        return storage.reference.child("scooter.png")
    }

    fun changeReserveStatus() {
        if (scooter.value?.isAvailable == true) {
            reserveScooter()
        } else {
            unreserveScooter()
        }
    }

    private fun updateScooter(scooter: Scooter) {
        this.scooter.value = scooter
        auth.currentUser?.let {
            database.child("scooters")
                .child(scooterId)
                .setValue(scooter)
        }
    }

    private fun updateRide(ride: Ride) {
        rideId?.let { rideId ->
            this.ride.value = ride
            auth.currentUser?.let {
                database.child("rides")
                    .child(it.uid)
                    .child(rideId)
                    .setValue(ride)
            }
        }
    }

    private fun reserveScooter() {
        scooter.value?.let {
            it.isAvailable = false
            updateScooter(it)
        }

        auth.currentUser?.let { user ->
            val uid = database.child("rides")
                .child(user.uid)
                .push()
                .key

            uid?.let {
                rideId = it
                updateRide(
                    Ride(
                        scooterId,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0.0,
                        RideStatus.RESERVED,
                    ),
                )
            }
        }
    }

    private fun unreserveScooter() {
        scooter.value?.let {
            it.isAvailable = true
            updateScooter(it)
        }

        ride.value?.let {
            it.endTime = System.currentTimeMillis()
            it.status = RideStatus.CANCELLED
            updateRide(it)
            rideId = null
            ride.value = null
        }
    }

    fun endRide() {
        scooter.value?.let {
            it.timestamp = System.currentTimeMillis()
            it.isAvailable = true
            it.latitude = getLocation().latitude
            it.longitude = getLocation().longitude
            it.currentUser = null
            it.imageIsUpdated = false
            updateScooter(it)
        }

        ride.value?.let {
            it.endTime = System.currentTimeMillis()
            it.status = RideStatus.ENDED
            it.endLatitude = getLocation().latitude
            it.endLongitude = getLocation().longitude
            it.price = it.startTime?.let { st ->
                it.endTime?.let { et ->
                    calculatePrice(st, et)
                }
            }
            updateRide(it)
            ride.value = null
        }
    }

    fun checkScooterImageUpdate(listener: OnGetDataListener) {
        database.child("scooters")
            .child(scooterId)
            .child("imageIsUpdated")
            .get()
            .addOnSuccessListener { snapshot ->
                listener.onSuccess(snapshot)
            }
    }
}

class ScooterDetailsViewModelFactory(
    private val scooterId: String,
    private val rideId: String?,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScooterDetailsViewModel::class.java)) {
            return ScooterDetailsViewModel(scooterId, rideId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
