package dk.itu.moapd.scootersharing.jonli.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.*
import kotlin.collections.ArrayList

abstract class BaseFragment : Fragment() {

    companion object {
        private const val ALL_PERMISSIONS_RESULT = 1011
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

    fun checkPermission() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED

    private fun Address.toAddressString(): String {
        val address = this
        val stringBuilder = StringBuilder()
        stringBuilder.apply {
            append(address.getAddressLine(0)).append("\n")
            append(address.locality).append("\n")
            append(address.postalCode).append("\n")
            append(address.countryName)
        }
        return stringBuilder.toString()
    }

    fun getAddress(latitude: Double, longitude: Double, callback: (String) -> Unit) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        geocoder.getFromLocation(
            latitude,
            longitude,
            1,
        )?.let { addresses ->
            addresses.firstOrNull()?.toAddressString()?.let { address ->
                callback(address)
            }
        }
    }

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
