package dk.itu.moapd.scootersharing.jonli.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.scootersharing.jonli.models.Ride
import kotlinx.coroutines.launch

class RideListViewModel : BaseViewModel() {

    var rides = MutableLiveData<FirebaseRecyclerOptions<Ride>?>(null)

    init {
        viewModelScope.launch {
            getRides()
        }
    }

    private fun getRides() {
        auth.currentUser?.let {
            val query = database.child("scooters")

            val options = FirebaseRecyclerOptions.Builder<Ride>()
                .setQuery(query, Ride::class.java)
                .build()

            rides.value = options
        }
    }
}

class RideListViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RideListViewModel::class.java)) {
            return RideListViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
