package dk.itu.moapd.scootersharing.jonli.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dk.itu.moapd.scootersharing.jonli.models.Scooter
import kotlinx.coroutines.launch

class MapViewModel : BaseViewModel() {

    var scooters = MutableLiveData<ArrayList<Pair<String?, Scooter?>>?>(null)

    init {
        viewModelScope.launch {
            getScooters()
        }
    }

    private fun getScooters() {
        scooters.value = ArrayList()
        auth.currentUser?.let {
            val ref = database.child("scooters")
            val eventListener: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val key = snapshot.key
                        val scooter = snapshot.getValue(Scooter::class.java)
                        scooters.value?.add(Pair(key, scooter))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println(databaseError.message)
                }
            }
            ref.addListenerForSingleValueEvent(eventListener)
        }
    }
}

class MapViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
