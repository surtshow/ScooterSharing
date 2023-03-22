package dk.itu.moapd.scootersharing.jonli.models

/**
 * This class represents a scooter.
 */
data class Scooter(
    val name: String,
    var location: String,
    var timestamp: Long = System.currentTimeMillis(),
) {
    override fun toString(): String {
        return "[Scooter] $name is placed at $location"
    }
}
