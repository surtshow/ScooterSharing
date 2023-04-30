package dk.itu.moapd.scootersharing.jonli.services

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.google.android.gms.location.* // ktlint-disable no-wildcard-imports
import com.google.android.gms.maps.model.LatLng
import dk.itu.moapd.scootersharing.jonli.activities.MainActivity

interface LocationListener {
    fun onLocationChanged(location: LatLng)
}

class LocationService : Service() {

    private var listener: LocationListener? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback

    companion object {
        private const val ALL_PERMISSIONS_RESULT = 1011
    }

    private val binder = LocationBinder()

    inner class LocationBinder : Binder() {
        fun getService(): LocationService = this@LocationService
    }

    override fun onBind(intent: Intent): IBinder {
        startLocationAwareness()
        subscribeToLocationUpdates()
        return binder
    }

    fun setListener(listener: LocationListener) {
        this.listener = listener
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

    fun getLastLocation(): LatLng? {
        if (checkPermission()) {
            return null
        }

        val location = fusedLocationProviderClient.lastLocation.result
        return LatLng(location.latitude, location.longitude)
    }

    private fun startLocationAwareness() {
        requestLocationPermission()

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    listener?.onLocationChanged(LatLng(it.latitude, it.longitude))
                }
            }
        }
    }

    @Suppress("MagicNumber")
    private fun subscribeToLocationUpdates() {
        // Check if the user allows the application to access the location-aware resources.
        if (checkPermission()) {
            return
        }

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

    private fun requestLocationPermission() {
        val permissions: Array<String> = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )

        val permissionsToRequest = permissionsToRequest(permissions)

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions(
                MainActivity(),
                permissionsToRequest.toTypedArray(),
                ALL_PERMISSIONS_RESULT,
            )
        }
    }

    private fun permissionsToRequest(permissions: Array<String>): ArrayList<String> {
        val result: ArrayList<String> = ArrayList()
        for (permission in permissions)
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                result.add(permission)
            }
        return result
    }

    fun checkPermission() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
}
