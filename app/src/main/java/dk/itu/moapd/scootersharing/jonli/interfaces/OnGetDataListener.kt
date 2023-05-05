package dk.itu.moapd.scootersharing.jonli.interfaces

import com.google.firebase.database.DataSnapshot

interface OnGetDataListener {
    fun onSuccess(snapshot: DataSnapshot)
}
