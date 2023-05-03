package dk.itu.moapd.scootersharing.jonli.fragments

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.budiyev.android.codescanner.* // ktlint-disable no-wildcard-imports
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentScannerBinding
import dk.itu.moapd.scootersharing.jonli.services.LocationService
import dk.itu.moapd.scootersharing.jonli.utils.LocationReceiver
import dk.itu.moapd.scootersharing.jonli.viewmodels.ScannerViewModel
import dk.itu.moapd.scootersharing.jonli.viewmodels.ScannerViewModelFactory
import kotlinx.coroutines.launch

class ScannerFragment : BaseFragment() {

    private lateinit var binding: FragmentScannerBinding
    private lateinit var viewModel: ScannerViewModel
    private lateinit var locationReceiver: LocationReceiver
    private lateinit var codeScanner: CodeScanner
    private lateinit var qrCode: String

    private val args: ScannerFragmentArgs by navArgs()

    private var mPermissionGranted = false

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentScannerBinding.inflate(inflater, container, false)
        val view = binding.root

        val viewModelFactory = ScannerViewModelFactory(args.scooterId)
        viewModel = ViewModelProvider(this, viewModelFactory)[ScannerViewModel::class.java]

        requestLocationPermission()
        locationReceiver = LocationReceiver(viewModel)

        val scannerView = binding.scannerView

        codeScanner = CodeScanner(requireContext(), scannerView)

        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            lifecycleScope.launch {
                if (checkPermission()) {
                    requestLocationPermission()
                } else {
                    qrCode = it.text
                    viewModel.getLocation().let {
                        getAddress(it.latitude, it.longitude) { address ->
                            viewModel.address = address
                        }
                    }
                    viewModel.scanQrCode(it.text)
                }
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            lifecycleScope.launch {
                Toast.makeText(
                    requireContext(),
                    "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG,
                ).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                mPermissionGranted = false
                requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_PERMISSIONS)
            } else {
                mPermissionGranted = true
            }
        } else {
            mPermissionGranted = true
        }
        setupObservers()

        return view
    }

    private fun setupObservers() {
        viewModel.ride.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController()
                    .navigate(
                        ScannerFragmentDirections.actionScannerFragmentToScooterDetailsFragment(
                            qrCode,
                            viewModel.rideId,
                        ),
                    )
                viewModel.resetValues()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true
                codeScanner.startPreview()
            } else {
                mPermissionGranted = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Intent(requireContext(), LocationService::class.java).also {
            requireContext().startService(it)
        }
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                locationReceiver,
                IntentFilter("ACTION_BROADCAST_LOCATION"),
            )
        if (mPermissionGranted) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        Intent(requireContext(), LocationService::class.java).also {
            requireContext().stopService(it)
        }
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(locationReceiver)
        codeScanner.releaseResources()
        super.onPause()
    }
}
