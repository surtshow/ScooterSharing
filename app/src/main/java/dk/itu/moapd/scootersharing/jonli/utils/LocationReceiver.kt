package dk.itu.moapd.scootersharing.jonli.utils

import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import dk.itu.moapd.scootersharing.jonli.viewmodels.LocationViewModel

class LocationReceiver(
    private val viewModel: LocationViewModel,
) : BroadcastReceiver() {

    private var location = LatLng(0.0, 0.0)

    fun getReceiverLocation(): LatLng {
        return location
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(ContentValues.TAG, "Received location update")
        if (intent != null) {
            intent.getParcelableExtra<LatLng>("EXTRA_LOCATION")?.let {
                Log.d(ContentValues.TAG, "Location: ${it.latitude}, ${it.longitude}")
                location = it
                viewModel.updateLocation(location)
            }
        }
    }
}
