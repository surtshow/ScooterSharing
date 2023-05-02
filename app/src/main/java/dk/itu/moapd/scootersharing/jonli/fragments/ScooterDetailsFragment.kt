package dk.itu.moapd.scootersharing.jonli.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.location.* // ktlint-disable no-wildcard-imports
import com.google.android.gms.maps.model.LatLng
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentScooterDetailsBinding
import dk.itu.moapd.scootersharing.jonli.enumerators.RideStatus
import dk.itu.moapd.scootersharing.jonli.viewmodels.ScooterDetailsViewModel
import dk.itu.moapd.scootersharing.jonli.viewmodels.ScooterDetailsViewModelFactory

class ScooterDetailsFragment : Fragment() {

    private lateinit var binding: FragmentScooterDetailsBinding
    private lateinit var viewModel: ScooterDetailsViewModel

//    private var locationService: LocationService? = null
//
//    private val locationServiceConnection = object : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            val binder = service as LocationService.LocationBinder
//            locationService = binder.getService()
//            locationService?.setListener(this@ScooterDetailsFragment)
//        }
//
//        override fun onServiceDisconnected(name: ComponentName?) {
//            locationService = null
//        }
//    }

    private val args: ScooterDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentScooterDetailsBinding.inflate(layoutInflater, container, false)

//        Intent(requireContext(), LocationService::class.java).also {
//            requireContext().bindService(it, locationServiceConnection, 0)
//        }

        val viewModelFactory = ScooterDetailsViewModelFactory(args.scooterId)
        viewModel = ViewModelProvider(this, viewModelFactory)[ScooterDetailsViewModel::class.java]

        startLocationAwareness()

        setupObservers()
        setupListeners()
        return binding.root
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        Intent(requireContext(), LocationService::class.java).also {
//            requireContext().unbindService(locationServiceConnection)
//        }
//    }

//    override fun onResume() {
//        super.onResume()
//        Intent(requireContext(), LocationService::class.java).also {
//            requireContext().bindService(it, locationServiceConnection, 0)
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        Intent(requireContext(), LocationService::class.java).also {
//            requireContext().stopService(it)
//        }
//    }

    override fun onResume() {
        super.onResume()
        subscribeToLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        unsubscribeToLocationUpdates()
    }

    private fun setupObservers() {
        viewModel.scooter.observe(viewLifecycleOwner) {
            it?.let {
                binding.scooterName.text = it.name
                // binding.scooterDescription.text = it.toString()

                if (it.isAvailable) {
                    binding.reserveButton.text = "Reserve"
                } else {
                    binding.reserveButton.text = "Cancel reservation"
                }

                binding.scooterPicture.setImageResource(0)

                // Download and set an image into the ImageView.
                viewModel.getScooterImage()
                    .downloadUrl
                    .addOnSuccessListener { uri ->
                        Glide.with(this)
                            .load(uri)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .centerCrop()
                            .into(binding.scooterPicture)
                    }
            }
        }
        viewModel.ride.observe(viewLifecycleOwner) {
            it?.let {
                if (it.status == RideStatus.STARTED) {
                    binding.reserveButton.isEnabled = false
                    binding.startButton.text = "End ride"
                } else {
                    binding.reserveButton.isEnabled = true
                    binding.startButton.text = "Start ride"
                }
            }
        }
    }

    private fun setupListeners() {
        binding.reserveButton.setOnClickListener {
            viewModel.changeReserveStatus()
        }

        binding.startButton.setOnClickListener {
            viewModel.changeStartStatus()
        }

        binding.photoButton.setOnClickListener {
            findNavController()
                .navigate(ScooterDetailsFragmentDirections.actionScooterDetailsFragmentToCameraFragment(args.scooterId))
        }
    }

//    override fun onLocationChanged(location: LatLng) {
//        binding.scooterDescription.text = "${location.latitude} - ${location.longitude}"
//        viewModel.updateLocation(location)
//    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback

    companion object {
        private const val ALL_PERMISSIONS_RESULT = 1011
    }

    private fun startLocationAwareness() {
        requestLocationPermission()

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    binding.scooterDescription.text = "${it.latitude} - ${it.longitude}"
                    viewModel.updateLocation(LatLng(it.latitude, it.longitude))
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
                permissionsToRequest.toTypedArray(),
                ALL_PERMISSIONS_RESULT,
            )
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

    private fun checkPermission() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
}
