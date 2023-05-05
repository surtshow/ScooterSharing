package dk.itu.moapd.scootersharing.jonli.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.hardware.SensorManager
import dk.itu.moapd.scootersharing.jonli.models.Scooter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.* // ktlint-disable no-wildcard-imports

object Utils {

    fun getCloseScooter(scooters: ArrayList<Pair<String?, Scooter?>>, latitude: Double, longitude: Double): String? {
        var closestScooter: String? = null
        var closestDistance = Double.MAX_VALUE

        for (scooter in scooters) {
            val distance = getDistance(
                latitude,
                longitude,
                scooter.second?.latitude,
                scooter.second?.longitude,
            )
            distance?.let {
                if (distance < closestDistance && scooter.second?.isAvailable == true) {
                    closestScooter = scooter.first
                    closestDistance = distance
                }
            }
        }
        return closestScooter
    }

    fun getDistance(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double?,
        endLongitude: Double?,
    ): Double? {
        if (endLatitude == null || endLongitude == null) return null
        val earthRadius = 6371
        val dLat = Math.toRadians(endLatitude - startLatitude)
        val dLng = Math.toRadians(endLongitude - startLongitude)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(startLatitude)) * cos(Math.toRadians(endLatitude)) *
            sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    fun Float.normalize(): Int {
        val norm = min(max(this, -SensorManager.STANDARD_GRAVITY), SensorManager.STANDARD_GRAVITY)
        return (
            (norm + SensorManager.STANDARD_GRAVITY) /
                (2f * SensorManager.STANDARD_GRAVITY) * 100
            ).toInt()
    }

    fun Long.formatDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = Date(this)
        return dateFormat.format(date)
    }

    fun Double.asPrice(): String {
        val formatted = String.format("%.2f", this)
        return "$formatted DKK"
    }

    @Suppress("MagicNumber")
    fun calculatePrice(startTime: Long, endTime: Long): Double {
        val diff = endTime - startTime
        return diff * 0.0000139
    }

    fun Bitmap.fixRotation(float: Float = 90f): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(float)
        return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
    }
}
