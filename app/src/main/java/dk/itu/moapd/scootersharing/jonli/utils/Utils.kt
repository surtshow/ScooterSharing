package dk.itu.moapd.scootersharing.jonli.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import java.util.*

object Utils {

    fun Bitmap.fixRotation(float: Float = 90f): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(float)
        return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
    }
}
