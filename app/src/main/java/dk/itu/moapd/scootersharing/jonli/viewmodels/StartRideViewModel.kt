package dk.itu.moapd.scootersharing.jonli.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.ktx.database
import dk.itu.moapd.scootersharing.jonli.models.Scooter
import java.util.*

class StartRideViewModel : BaseViewModel() {

    fun createRide(name: String, location: String): Scooter {
        val scooter = Scooter(name, location)
        auth.currentUser?.let { user ->
            val uid = database.child("scooters")
                .child(user.uid)
                .push()
                .key

            uid?.let {
                database.child("scooters")
                    .child(user.uid)
                    .child(it)
                    .setValue(scooter)
            }
        }
        return scooter
    }
}

class StartRideViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartRideViewModel::class.java)) {
            return StartRideViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
