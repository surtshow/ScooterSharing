package dk.itu.moapd.scootersharing.jonli.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.*
import kotlin.collections.ArrayList

abstract class BaseFragment : Fragment() {

    companion object {
        private const val ALL_PERMISSIONS_RESULT = 1011

        private const val REQUEST_CODE_PERMISSIONS = 10

        @RequiresApi(Build.VERSION_CODES.S)
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.HIGH_SAMPLING_RATE_SENSORS,
        )
    }

    fun requestLocationPermission() {
        val permissions: Array<String> = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )

        val permissionsToRequest = permissionsToRequest(permissions)

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions(permissionsToRequest.toTypedArray(), ALL_PERMISSIONS_RESULT)
        }
    }

    private fun permissionsToRequest(permissions: Array<String>): ArrayList<String> {
        val result: ArrayList<String> = ArrayList()
        for (permission in permissions)
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                result.add(permission)
            }
        return result
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun requestSensorPermission() {
        val permissions: Array<String> = arrayOf(
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.HIGH_SAMPLING_RATE_SENSORS,
        )

        val permissionsToRequest = permissionsToRequest(permissions)

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions(permissionsToRequest.toTypedArray(), ALL_PERMISSIONS_RESULT)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(),
            it,
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkLocationPermission() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

    fun checkPermission() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED

    @Suppress("LongParameterList")
    fun dialog(
        title: String,
        message: String,
        positiveButton: String,
        negativeButton: String,
        positiveMethod: () -> Unit,
        negativeMethod: () -> Unit,
    ) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                positiveButton,
            ) { dialog, _ ->
                dialog.dismiss()
                positiveMethod()
            }
            .setNegativeButton(
                negativeButton,
            ) { dialog, _ ->
                negativeMethod()
            }
        builder.create()
        builder.show()
    }
}
