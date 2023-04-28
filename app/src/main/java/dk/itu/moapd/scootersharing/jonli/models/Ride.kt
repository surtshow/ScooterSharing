package dk.itu.moapd.scootersharing.jonli.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Ride(
    val scooter: Scooter? = null,
    var startLocation: String? = null,
    var endLocation: String? = null,
    var startTime: Long = System.currentTimeMillis(),
    var endTime: Long? = null,
)
