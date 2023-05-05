package dk.itu.moapd.scootersharing.jonli.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

abstract class BaseViewModel : ViewModel() {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    var database: DatabaseReference = Firebase.database(DATABASE_URL).reference

    val storage = Firebase.storage(BUCKET_URL)

    companion object {
        private const val DATABASE_URL = "https://scooter-sharing-b2ed6-default-rtdb.europe-west1.firebasedatabase.app/"
        private const val BUCKET_URL = "gs://scooter-sharing-b2ed6.appspot.com"
    }
}
