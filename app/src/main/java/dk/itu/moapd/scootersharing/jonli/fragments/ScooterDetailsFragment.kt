package dk.itu.moapd.scootersharing.jonli.fragments

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
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
import com.google.firebase.database.DataSnapshot
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentScooterDetailsBinding
import dk.itu.moapd.scootersharing.jonli.enumerators.RideStatus
import dk.itu.moapd.scootersharing.jonli.interfaces.OnGetDataListener
import dk.itu.moapd.scootersharing.jonli.services.LocationService
import dk.itu.moapd.scootersharing.jonli.utils.LocationReceiver
import dk.itu.moapd.scootersharing.jonli.viewmodels.ScooterDetailsViewModel
import dk.itu.moapd.scootersharing.jonli.viewmodels.ScooterDetailsViewModelFactory

class ScooterDetailsFragment : BaseFragment() {

    private lateinit var binding: FragmentScooterDetailsBinding
    private lateinit var viewModel: ScooterDetailsViewModel
    private lateinit var locationReceiver: LocationReceiver

    private var rideIsActive = false

    private val args: ScooterDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentScooterDetailsBinding.inflate(layoutInflater, container, false)

        val viewModelFactory = ScooterDetailsViewModelFactory(args.scooterId, args.rideId)
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

                it.latitude?.let { latitude ->
                    it.longitude?.let { longitude ->
                        getAddress(latitude, longitude) { str ->
                            binding.scooterDescription.text = str
                        }
                    }
                }

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
            rideIsActive = false
            binding.reserveButton.isEnabled = true
            binding.photoButton.isEnabled = false
            binding.startButton.text = "Start ride"

            it?.let {
                if (it.status == RideStatus.STARTED) {
                    rideIsActive = true
                    binding.reserveButton.isEnabled = false
                    binding.photoButton.isEnabled = true
                    binding.startButton.text = "End ride"
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
            if (rideIsActive) {
                viewModel.checkScooterImageUpdate(object : OnGetDataListener {
                    override fun onSuccess(snapshot: DataSnapshot) {
                        snapshot.getValue(Boolean::class.java)?.let { isUpdated ->
                            if (isUpdated) {
                                viewModel.endRide()
                                updateLocationText()
                            } else {
                                dialog(
                                    "Picture not updated",
                                    "Please update the picture before ending the ride. This will help the next user to find the scooter.",
                                    "Take picture",
                                    "End ride",
                                    {
                                        findNavController()
                                            .navigate(
                                                ScooterDetailsFragmentDirections
                                                    .actionScooterDetailsFragmentToCameraFragment(args.scooterId),
                                            )
                                    },
                                    {
                                        viewModel.endRide()
                                        updateLocationText()
                                    },
                                )
                            }
                        }
                    }
                })
            } else {
                findNavController()
                    .navigate(
                        ScooterDetailsFragmentDirections.actionScooterDetailsFragmentToScannerFragment(
                            args.scooterId,
                        ),
                    )
            }
        }

        binding.photoButton.setOnClickListener {
            findNavController()
                .navigate(ScooterDetailsFragmentDirections.actionScooterDetailsFragmentToCameraFragment(args.scooterId))
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun updateLocationText() {
        viewModel.getLocation().let {
            getAddress(it.latitude, it.longitude) { address ->
                viewModel.updateLocationString(address)
            }
        }
    }
}
