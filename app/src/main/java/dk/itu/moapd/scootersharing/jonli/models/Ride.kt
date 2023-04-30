package dk.itu.moapd.scootersharing.jonli.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Ride(
    val scooter: Scooter? = null,
    var startLocation: Pair<Double, Double>? = null,
    var endLocation: Pair<Double, Double>? = null,
    var startTime: Long = System.currentTimeMillis(),
    var endTime: Long? = null,
    var price: Double? = null,
)
