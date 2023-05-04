package dk.itu.moapd.scootersharing.jonli.fragments

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Address
import android.location.Geocoder
import android.media.MediaPlayer
import android.os.Build
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
import dk.itu.moapd.scootersharing.jonli.R
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentScooterDetailsBinding
import dk.itu.moapd.scootersharing.jonli.enumerators.RideStatus
import dk.itu.moapd.scootersharing.jonli.interfaces.OnGetDataListener
import dk.itu.moapd.scootersharing.jonli.services.LocationService
import dk.itu.moapd.scootersharing.jonli.utils.LocationReceiver
import dk.itu.moapd.scootersharing.jonli.utils.SpeedReceiver
import dk.itu.moapd.scootersharing.jonli.viewmodels.ScooterDetailsViewModel
import dk.itu.moapd.scootersharing.jonli.viewmodels.ScooterDetailsViewModelFactory
import java.util.*

class ScooterDetailsFragment : BaseFragment() {

    private lateinit var binding: FragmentScooterDetailsBinding
    private lateinit var viewModel: ScooterDetailsViewModel
    private lateinit var locationReceiver: LocationReceiver
    private lateinit var speedReceiver: SpeedReceiver
    private lateinit var sensorManager: SensorManager
    private lateinit var mediaPlayer: MediaPlayer

    private var rideIsActive = false

    private val args: ScooterDetailsFragmentArgs by navArgs()

    private val accelerationListener: SensorEventListener = object : SensorEventListener {

        override fun onSensorChanged(event: SensorEvent) {
            binding.apply {
                val x = event.values[0] + event.values[1] + event.values[2]
                val acc = kotlin.math.abs(x).toInt() - 10

                circularProgressIndicator.progress = acc

                if (acc > 50) {
                    mediaPlayer.start()
                }
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // Do nothing
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentScooterDetailsBinding.inflate(layoutInflater, container, false)

        val viewModelFactory = ScooterDetailsViewModelFactory(args.scooterId, args.rideId)
        viewModel = ViewModelProvider(this, viewModelFactory)[ScooterDetailsViewModel::class.java]

        requestLocationPermission()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestSensorPermission()
        }

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.beep)

        locationReceiver = LocationReceiver(viewModel)
        speedReceiver = SpeedReceiver(binding.awesomeSpeedometer) { speed ->
            if (speed > 8.0f) {
                mediaPlayer.start()
            }
        }

        val service = Context.SENSOR_SERVICE
        sensorManager = requireActivity().getSystemService(service) as SensorManager

        setupObservers()
        setupListeners()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // TODO
        Intent(requireContext(), LocationService::class.java).also {
            requireContext().startService(it)
        }
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                locationReceiver,
                IntentFilter("ACTION_BROADCAST_LOCATION"),
            )
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                speedReceiver,
                IntentFilter("ACTION_BROADCAST_SPEED"),
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (allPermissionsGranted()) {
                startAcceleration()
            }
        } else {
            startAcceleration()
        }
    }

    private fun startAcceleration() {
        val acceleration = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (acceleration != null) {
            sensorManager.registerListener(
                accelerationListener,
                acceleration,
                SensorManager.SENSOR_DELAY_NORMAL,
            )
        }
    }

    override fun onPause() {
        super.onPause()

        Intent(requireContext(), LocationService::class.java).also {
            requireContext().stopService(it)
        }
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(locationReceiver)

        sensorManager.unregisterListener(accelerationListener)
    }

    private fun setupObservers() {
        binding.scooterDescription.visibility = View.GONE
        viewModel.scooter.observe(viewLifecycleOwner) {
            it?.let {
                binding.scooterName.text = it.name

                it.latitude?.let { latitude ->
                    it.longitude?.let { longitude ->
                        // TODO: Uncomment to test getAddress
                        // getAddress(latitude, longitude)
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

    private fun getAddress(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        geocoder.getFromLocation(
            latitude,
            longitude,
            1,
        )?.let { addresses ->
            addresses.firstOrNull()?.toAddressString()?.let { address ->
                binding.scooterDescription.visibility = View.VISIBLE
                binding.scooterDescription.text = address
            }
        }
    }
}
