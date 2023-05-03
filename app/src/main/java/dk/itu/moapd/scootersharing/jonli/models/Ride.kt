package dk.itu.moapd.scootersharing.jonli.models

import com.google.firebase.database.IgnoreExtraProperties
import dk.itu.moapd.scootersharing.jonli.enumerators.RideStatus

@IgnoreExtraProperties
data class Ride(
    val scooterId: String? = null,
    var startLatitude: Double? = null,
    var startLongitude: Double? = null,
    var endLatitude: Double? = null,
    var endLongitude: Double? = null,
    var startLocation: String? = null,
    var endLocation: String? = null,
    var startTime: Long? = null,
    var endTime: Long? = null,
    var price: Double? = null,
    var status: RideStatus = RideStatus.RESERVED,
)
