package dk.itu.moapd.scootersharing.jonli.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.PointF
import com.github.anastr.speedviewlib.AwesomeSpeedometer

class SpeedReceiver(
    private val speedometer: AwesomeSpeedometer,
    private val callback: (Float) -> Unit = {},
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.getParcelableExtra<PointF>("EXTRA_SPEED")?.let {
            speedometer.speedTo(it.x)
            callback(it.x)
        }
    }
}
