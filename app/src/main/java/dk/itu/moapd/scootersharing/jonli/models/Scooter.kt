package dk.itu.moapd.scootersharing.jonli.models

import com.google.firebase.database.IgnoreExtraProperties

/**
 * This class represents a scooter.
 */
@IgnoreExtraProperties
data class Scooter(
    val name: String? = null,
    var location: String? = null,
    var timestamp: Long = System.currentTimeMillis(),
    var image: String? = null,
) {
    override fun toString(): String {
        return "[Scooter] $name is placed at $location"
    }
}
