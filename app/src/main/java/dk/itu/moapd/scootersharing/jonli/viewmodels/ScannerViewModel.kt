package dk.itu.moapd.scootersharing.jonli.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dk.itu.moapd.scootersharing.jonli.enumerators.RideStatus
import dk.itu.moapd.scootersharing.jonli.interfaces.OnGetDataListener
import dk.itu.moapd.scootersharing.jonli.models.Ride
import dk.itu.moapd.scootersharing.jonli.models.Scooter

class ScannerViewModel(
    private val scooterId: String?,
) : LocationViewModel() {

    private lateinit var qrCode: String
    var rideId: String? = null
    private var activeRide: Ride? = null

    var ride = MutableLiveData<Ride?>(null)

    fun scanQrCode(qrCode: String) {
        this.qrCode = qrCode

        isScooter(qrCode) {
            if (it) {
                findRides()
            }
        }
    }

    private fun findRides() {
        getScootersRides(
            qrCode,
            object : OnGetDataListener {
                override fun onSuccess(snapshot: DataSnapshot) {
                    for (snap in snapshot.children) {
                        val ride = snap.getValue(Ride::class.java)
                        if (ride != null) {
                            if (
                                ride.status == RideStatus.RESERVED || ride.status == RideStatus.STARTED
                            ) {
                                rideId = snap.key
                                activeRide = ride
                            }
                        }
                    }
                    startRide()
                }
            },
        )
    }

    private fun getScootersRides(qrCode: String, listener: OnGetDataListener) {
        auth.currentUser?.let {
            val ref = database.child("rides")
                .child(it.uid)
                .orderByChild("scooterId")
                .equalTo(qrCode)

            val eventListener: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    listener.onSuccess(dataSnapshot)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println(databaseError.message)
                }
            }
            ref.addListenerForSingleValueEvent(eventListener)
        }
    }

    private fun startRide() {
        if (rideId != null) {
            if (activeRide?.status == RideStatus.RESERVED) {
                activeRide?.let {
                    it.startTime = System.currentTimeMillis()
                    it.status = RideStatus.STARTED
                    it.startLatitude = getLocation().latitude
                    it.startLongitude = getLocation().longitude
                    updateRide(it)
                }
            }
            activeRide?.let {
                updateRide(it)
            }
        } else {
            updateScooter(qrCode)
            auth.currentUser?.let { user ->
                val uid = database.child("rides")
                    .child(user.uid)
                    .push()
                    .key

                uid?.let {
                    rideId = it
                    updateRide(
                        Ride(
                            qrCode,
                            getLocation().latitude,
                            getLocation().longitude,
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

    private fun isScooter(qrCode: String, callback: (Boolean) -> Unit) {
        database.child("scooters")
            .child(qrCode)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.getValue(Scooter::class.java)?.let { scooter ->
                    callback(true)
                }
            }
        callback(false)
    }

    private fun updateScooter(qrCode: String) {
        auth.currentUser?.let {
            database.child("scooters")
                .child(qrCode)
                .get()
                .addOnSuccessListener { snapshot ->
                    snapshot.getValue(Scooter::class.java)?.let { scooter ->
                        scooter.apply {
                            isAvailable = false
                            currentUser = it.uid
                        }
                        database.child("scooters")
                            .child(qrCode)
                            .setValue(scooter)
                    }
                }
        }
    }

    private fun updateRide(ride: Ride) {
        rideId?.let { rideId ->
            auth.currentUser?.let {
                database.child("rides")
                    .child(it.uid)
                    .child(rideId)
                    .setValue(ride)
            }
            this.ride.value = ride
        }
    }

    fun resetValues() {
        rideId = null
        activeRide = null
        ride.value = null
    }
}

class ScannerViewModelFactory(
    private val scooterId: String?,
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScannerViewModel::class.java)) {
            return ScannerViewModel(scooterId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
