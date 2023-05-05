package dk.itu.moapd.scootersharing.jonli.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.scootersharing.jonli.enumerators.RideStatus
import dk.itu.moapd.scootersharing.jonli.models.Ride

class RideListViewModel(
    private val lifecycleOwner: LifecycleOwner,
    private val rideType: RideStatus,
) : BaseViewModel() {

    private lateinit var query: com.google.firebase.database.Query

    fun getRides(rideType: RideStatus): FirebaseRecyclerOptions<Ride>? {
        auth.currentUser?.let {
            if (rideType == RideStatus.STARTED) {
                query = database.child("rides")
                    .child(it.uid)
                    .orderByChild("status").equalTo("ACTIVE")
            } else if (rideType == RideStatus.ENDED) {
                query = database.child("rides")
                    .child(it.uid)
                    .orderByChild("status").equalTo("ENDED")
            }

            val options = FirebaseRecyclerOptions.Builder<Ride>()
                .setQuery(query, Ride::class.java)
                .setLifecycleOwner(lifecycleOwner)
                .build()
        }
        return null
    }
}

class RideListViewModelFactory(
    private val lifecycleOwner: LifecycleOwner,
    private val rideType: RideStatus,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RideListViewModel::class.java)) {
            return RideListViewModel(lifecycleOwner, rideType) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
