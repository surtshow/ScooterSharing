package dk.itu.moapd.scootersharing.jonli.fragments

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.location.* // ktlint-disable no-wildcard-imports
import com.google.android.gms.maps.model.LatLng
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentScooterDetailsBinding
import dk.itu.moapd.scootersharing.jonli.enumerators.RideStatus
import dk.itu.moapd.scootersharing.jonli.services.LocationService
import dk.itu.moapd.scootersharing.jonli.viewmodels.ScooterDetailsViewModel
import dk.itu.moapd.scootersharing.jonli.viewmodels.ScooterDetailsViewModelFactory

class ScooterDetailsFragment : BaseFragment() {

    private class LocationReceiver(
        private val viewModel: ScooterDetailsViewModel,
    ) : BroadcastReceiver() {

        private var location = LatLng(0.0, 0.0)

        fun getReceiverLocation(): LatLng {
            return location
        }
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Received location update")
            if (intent != null) {
                intent.getParcelableExtra<LatLng>("EXTRA_LOCATION")?.let {
                    Log.d(TAG, "Location: ${it.latitude}, ${it.longitude}")
                    location = it
                    viewModel.updateLocation(location)
                }
            }
        }
    }

    private lateinit var binding: FragmentScooterDetailsBinding
    private lateinit var viewModel: ScooterDetailsViewModel
    private lateinit var locationReceiver: LocationReceiver

    private val args: ScooterDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentScooterDetailsBinding.inflate(layoutInflater, container, false)

        val viewModelFactory = ScooterDetailsViewModelFactory(args.scooterId)
        viewModel = ViewModelProvider(this, viewModelFactory)[ScooterDetailsViewModel::class.java]

        requestLocationPermission()

        locationReceiver = LocationReceiver(viewModel)

        setupObservers()
        setupListeners()
        return binding.root
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
    }

    override fun onPause() {
        super.onPause()
        Intent(requireContext(), LocationService::class.java).also {
            requireContext().stopService(it)
        }
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(locationReceiver)
    }

    private fun setupObservers() {
        viewModel.scooter.observe(viewLifecycleOwner) {
            it?.let {
                binding.scooterName.text = it.name
                binding.scooterDescription.text = locationReceiver.getReceiverLocation().toString()

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
            if (checkPermission()) {
                requestLocationPermission()
            } else {
                viewModel.changeReserveStatus()
            }
        }

        binding.startButton.setOnClickListener {
            if (checkPermission()) {
                requestLocationPermission()
            } else {
                viewModel.changeStartStatus()
            }
        }

        binding.photoButton.setOnClickListener {
            findNavController()
                .navigate(ScooterDetailsFragmentDirections.actionScooterDetailsFragmentToCameraFragment(args.scooterId))
        }
    }
}
