package dk.itu.moapd.scootersharing.jonli

/**
 * This class represents a scooter.
 */
data class Scooter(var name: String, var location: String) {
    override fun toString(): String {
        return "[Scooter] $name is placed at $location"
    }
}
