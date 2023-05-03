package dk.itu.moapd.scootersharing.jonli.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.jonli.models.Scooter
import kotlinx.coroutines.launch
import java.util.*

class CameraViewModel(
    private val scooterId: String,
) : BaseViewModel() {

    init {
        viewModelScope.launch {
            getScooter()
        }
    }

    private lateinit var scooter: Scooter
    private lateinit var imageRef: StorageReference

    private fun getScooter() {
        auth.currentUser.let {
            viewModelScope.launch {
                database
                    .child("scooters")
                    .child(scooterId)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        snapshot.getValue(Scooter::class.java)?.let { scooter ->
                            this@CameraViewModel.scooter = scooter
                        }

                        scooter.name?.let {
                            imageRef = storage.reference.child(it.replace(" ", ""))
                        }
                    }
            }
        }
    }

    fun updateScooterPicture(imageByteArray: ByteArray, callback: () -> Unit) {
        viewModelScope.launch {
            imageRef.putBytes(imageByteArray)
        }
        scooter.image = imageRef.path
        scooter.imageIsUpdated = true
        auth.currentUser?.let {
            database.child("scooters")
                .child(scooterId)
                .setValue(scooter)
        }
        callback()
    }
}

class CameraViewModelFactory(
    private val scooterId: String,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            return CameraViewModel(scooterId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
