package dk.itu.moapd.scootersharing.jonli.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.scootersharing.jonli.enumerators.RideStatus
import dk.itu.moapd.scootersharing.jonli.models.Ride
import kotlinx.coroutines.launch

class RideListViewModel(
    private val lifecycleOwner: LifecycleOwner,
    private val rideType: RideStatus,
) : BaseViewModel() {

    var rides = MutableLiveData<FirebaseRecyclerOptions<Ride>?>(null)
    private lateinit var query: com.google.firebase.database.Query

    init {
        viewModelScope.launch {
            getRides(rideType)
        }
    }

    private fun getRides(rideType: RideStatus) {
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

            rides.value = options
        }
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
