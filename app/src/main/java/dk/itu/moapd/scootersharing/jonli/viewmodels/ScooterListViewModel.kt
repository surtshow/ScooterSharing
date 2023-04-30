package dk.itu.moapd.scootersharing.jonli.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.scootersharing.jonli.models.Scooter
import kotlinx.coroutines.launch

class ScooterListViewModel : BaseViewModel() {

    var scooters = MutableLiveData<FirebaseRecyclerOptions<Scooter>?>(null)

    init {
        viewModelScope.launch {
            getScooters()
        }
    }

    private fun getScooters() {
        auth.currentUser?.let {
            val query = database.child("scooters")
                .child(it.uid)

            val options = FirebaseRecyclerOptions.Builder<Scooter>()
                .setQuery(query, Scooter::class.java)
                .build()

            scooters.value = options
        }
    }
}

class ScooterListViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScooterListViewModel::class.java)) {
            return ScooterListViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
