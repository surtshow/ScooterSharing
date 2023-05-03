package dk.itu.moapd.scootersharing.jonli.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.scootersharing.jonli.models.Scooter
import kotlinx.coroutines.launch

class ScooterListViewModel(
    private val lifecycleOwner: LifecycleOwner,
) : BaseViewModel() {

    var scooters = MutableLiveData<FirebaseRecyclerOptions<Scooter>?>(null)

    init {
        viewModelScope.launch {
            getScooters()
        }
    }

    private fun getScooters() {
        auth.currentUser?.let {
            val query = database.child("scooters").orderByChild("available").equalTo(true)

            val options = FirebaseRecyclerOptions.Builder<Scooter>()
                .setQuery(query, Scooter::class.java)
                .setLifecycleOwner(lifecycleOwner)
                .build()

            scooters.value = options
        }
    }
}

class ScooterListViewModelFactory(
    private val lifecycleOwner: LifecycleOwner,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScooterListViewModel::class.java)) {
            return ScooterListViewModel(lifecycleOwner) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
