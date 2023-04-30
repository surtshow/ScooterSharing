package dk.itu.moapd.scootersharing.jonli.models

import com.google.firebase.database.IgnoreExtraProperties
import dk.itu.moapd.scootersharing.jonli.enumerators.RideStatus

@IgnoreExtraProperties
data class Ride(
    val scooterId: String? = null,
    var startLocation: Pair<Double, Double>? = null,
    var endLocation: Pair<Double, Double>? = null,
    var reserveTime: Long? = null,
    var startTime: Long? = null,
    var endTime: Long? = null,
    var price: Double? = null,
    var status: RideStatus = RideStatus.RESERVED,
)
