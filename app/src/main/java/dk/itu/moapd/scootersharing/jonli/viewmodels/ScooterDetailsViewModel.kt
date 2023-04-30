package dk.itu.moapd.scootersharing.jonli.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dk.itu.moapd.scootersharing.jonli.enumerators.RideStatus
import dk.itu.moapd.scootersharing.jonli.models.Ride
import dk.itu.moapd.scootersharing.jonli.models.Scooter
import kotlinx.coroutines.launch

class ScooterDetailsViewModel(
    private val scooterId: String,
) : BaseViewModel() {

    private lateinit var rideId: String
    private var location: LatLng = LatLng(0.0, 0.0)

    var scooter = MutableLiveData<Scooter?>(null)
    var ride = MutableLiveData<Ride?>(null)

    init {
        viewModelScope.launch {
            getScooter()
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

//    private fun getRide() {
//        auth.currentUser?.let {
//            database.child("rides")
//                .child(it.uid)
//                .orderByChild("scooterId").equalTo(scooterId)
//                .get()
//                .addOnSuccessListener { snapshot ->
//                    for (child in snapshot.children) {
//                        child.getValue(Ride::class.java)?.let { ride ->
//                            if (ride.status == RideStatus.STARTED || ride.status == RideStatus.RESERVED) {
//                                this.ride.value = ride
//                            }
//                        }
//                    }
//                }
//        }
//    }

    fun changeReserveStatus() {
        if (scooter.value?.isAvailable == true) {
            reserveScooter()
        } else {
            unreserveScooter()
        }
    }

    fun changeStartStatus() {
        if (ride.value?.status == RideStatus.STARTED) {
            endRide()
        } else {
            startRide()
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
        this.ride.value = ride
        auth.currentUser?.let {
            database.child("rides")
                .child(it.uid)
                .child(rideId)
                .setValue(ride)
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
                        System.currentTimeMillis(),
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
        }
    }

    private fun startRide() {
        scooter.value?.let {
            it.isAvailable = false
            updateScooter(it)
        }

        if (ride.value != null) {
            ride.value?.let {
                it.startTime = System.currentTimeMillis()
                it.status = RideStatus.STARTED
                it.startLocation = Pair(location.latitude, location.longitude)
                updateRide(it)
            }
        } else {
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
                            Pair(location.latitude, location.longitude),
                            null,
                            null,
                            System.currentTimeMillis(),
                            null,
                            0.0,
                            RideStatus.STARTED,
                        ),
                    )
                }
            }
        }
    }

    private fun endRide() {
        scooter.value?.let {
            it.timestamp = System.currentTimeMillis()
            it.isAvailable = true
            it.latitude = location.latitude
            it.longitude = location.longitude
            updateScooter(it)
        }

        ride.value?.let {
            it.endTime = System.currentTimeMillis()
            it.status = RideStatus.ENDED
            it.endLocation = Pair(location.latitude, location.longitude)
            updateRide(it)
            ride.value = null
        }
    }

    fun updateLocation(location: LatLng) {
        this.location = location
    }
}

class ScooterDetailsViewModelFactory(
    private val scooterId: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScooterDetailsViewModel::class.java)) {
            return ScooterDetailsViewModel(scooterId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
