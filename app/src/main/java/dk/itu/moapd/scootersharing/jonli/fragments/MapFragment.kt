package dk.itu.moapd.scootersharing.jonli.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.* // ktlint-disable no-wildcard-imports
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dk.itu.moapd.scootersharing.jonli.R
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentMapBinding
import dk.itu.moapd.scootersharing.jonli.viewmodels.MapViewModel
import dk.itu.moapd.scootersharing.jonli.viewmodels.MapViewModelFactory

class MapFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding

    private lateinit var viewModel: MapViewModel

    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMapBinding.inflate(layoutInflater, container, false)

        val viewModelFactory = MapViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory)[MapViewModel::class.java]

        requestLocationPermission()

        val gMapFragment =
            childFragmentManager.findFragmentById(binding.googleMaps.id) as SupportMapFragment
        gMapFragment.getMapAsync(this)

        return binding.root
    }

    private fun setupObservers() {
        viewModel.scooters.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) {
                    for (scooterPair in it) {
                        val scooter = scooterPair.second

                        scooter?.latitude?.let { latitude ->
                            scooter.longitude?.let { longitude ->
                                map.addMarker(
                                    MarkerOptions()
                                        .position(LatLng(latitude, longitude))
                                        .title(scooterPair.first),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.scannerFragment -> {
                    findNavController()
                        .navigate(MapFragmentDirections.actionMapFragmentToScannerFragment(null))
                    true
                }
                else -> false
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (checkLocationPermission()) {
            googleMap.isMyLocationEnabled = true
        }

        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        googleMap.uiSettings.apply {
            isCompassEnabled = true
            isIndoorLevelPickerEnabled = true
            isMyLocationButtonEnabled = true
            isRotateGesturesEnabled = true
            isScrollGesturesEnabled = true
            isTiltGesturesEnabled = true
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
        }
        map = googleMap

        val ituPosition = LatLng(55.6596, 12.5910)

        map.moveCamera(
            com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                ituPosition,
                15f,
            ),
        )

        map.setOnMarkerClickListener { marker ->
            val scooter = marker.title
            scooter?.let {
                val action = MapFragmentDirections.actionMapFragmentToScooterDetailsFragment(it, null)
                findNavController()
                    .navigate(action)
            }
            true
        }

        setupObservers()
        setupListeners()
    }
}
