package dk.itu.moapd.scootersharing.jonli.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import java.util.*

object Utils {

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
