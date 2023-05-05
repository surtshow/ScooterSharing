package dk.itu.moapd.scootersharing.jonli.services

import android.app.Service
import android.content.Intent
import android.graphics.PointF
import android.os.IBinder
import android.os.Looper
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.* // ktlint-disable no-wildcard-imports
import com.google.android.gms.maps.model.LatLng

class LocationService : Service() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback

    override fun onBind(intent: Intent): IBinder {
        TODO()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationAwareness()
        subscribeToLocationUpdates()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        unsubscribeToLocationUpdates()
        super.onDestroy()
    }

    private fun startLocationAwareness() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    newLocation(LatLng(it.latitude, it.longitude))
                    newSpeed(it.speed)
                }
            }
        }
    }

    private fun newLocation(location: LatLng) {
        val intent = Intent("ACTION_BROADCAST_LOCATION")
        intent.putExtra("EXTRA_LOCATION", location)

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun newSpeed(speed: Float) {
        val intent = Intent("ACTION_BROADCAST_SPEED")
        intent.putExtra("EXTRA_SPEED", PointF(speed, 0f))

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    @Suppress("MagicNumber")
    @SuppressWarnings("MissingPermission")
    private fun subscribeToLocationUpdates() {
        // Sets the accuracy and desired interval for active location updates.
        val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 5)
            .build()

        // Subscribe to location changes.
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper(),
        )
    }

    /**
     * Unsubscribes this application of getting the location changes from  the `locationCallback()`.
     */
    private fun unsubscribeToLocationUpdates() {
        // Unsubscribe to location changes.
        fusedLocationProviderClient
            .removeLocationUpdates(locationCallback)
    }
}
