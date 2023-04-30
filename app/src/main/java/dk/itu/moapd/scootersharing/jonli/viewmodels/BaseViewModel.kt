package dk.itu.moapd.scootersharing.jonli.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

abstract class BaseViewModel : ViewModel() {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    var database: DatabaseReference = Firebase.database(DATABASE_URL).reference

    companion object {
        private const val DATABASE_URL = "https://scooter-sharing-b2ed6-default-rtdb.europe-west1.firebasedatabase.app/"
    }
}
