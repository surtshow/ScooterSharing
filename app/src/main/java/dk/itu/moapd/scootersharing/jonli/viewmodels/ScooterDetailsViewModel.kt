package dk.itu.moapd.scootersharing.jonli.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dk.itu.moapd.scootersharing.jonli.models.Scooter
import kotlinx.coroutines.launch

class ScooterDetailsViewModel(
    private val scooterId: String,
) : BaseViewModel() {

    var scooter = MutableLiveData<Scooter?>(null)

    init {
        viewModelScope.launch {
            getScooter()
        }
    }

    private fun getScooter() {
        auth.currentUser?.let {
            database.child("scooters")
                .child(it.uid)
                .child(scooterId)
                .get()
                .addOnSuccessListener { snapshot ->
                    snapshot.getValue(Scooter::class.java)?.let { scooter ->
                        this.scooter.value = scooter
                    }
                }
        }
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
