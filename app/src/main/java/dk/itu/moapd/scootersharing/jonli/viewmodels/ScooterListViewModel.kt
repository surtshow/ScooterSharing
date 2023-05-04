package dk.itu.moapd.scootersharing.jonli.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dk.itu.moapd.scootersharing.jonli.models.Scooter
import dk.itu.moapd.scootersharing.jonli.utils.Utils.getCloseScooter
import kotlinx.coroutines.launch

class ScooterListViewModel(
    private val lifecycleOwner: LifecycleOwner,
) : LocationViewModel() {

    var scooterList = MutableLiveData<ArrayList<Pair<String?, Scooter?>>?>(null)

    init {
        viewModelScope.launch {
            getScooterList()
        }
    }

    private fun getScooterList() {
        scooterList.value = ArrayList()
        auth.currentUser?.let {
            val ref = database.child("scooters")
            val eventListener: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val key = snapshot.key
                        val scooter = snapshot.getValue(Scooter::class.java)
                        scooterList.value?.add(Pair(key, scooter))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println(databaseError.message)
                }
            }
            ref.addListenerForSingleValueEvent(eventListener)
        }
    }

    fun getNearestScooters(): String? {
        scooterList.value?.let {
            return getCloseScooter(it, getLocation().latitude, getLocation().longitude)
        }
        return null
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
