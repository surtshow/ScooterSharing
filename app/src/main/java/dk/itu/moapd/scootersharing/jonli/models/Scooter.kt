package dk.itu.moapd.scootersharing.jonli.models

import com.google.firebase.database.IgnoreExtraProperties

/**
 * This class represents a scooter.
 */
@IgnoreExtraProperties
data class Scooter(
    val name: String? = null,
    var timestamp: Long = System.currentTimeMillis(),
    var isAvailable: Boolean = true,
    var image: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var currentUser: String? = null,
    var imageIsUpdated: Boolean = false,
) {
    override fun toString(): String {
        return "[Scooter] $name"
    }
}
