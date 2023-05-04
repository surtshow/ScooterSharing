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
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.scootersharing.jonli.R
import dk.itu.moapd.scootersharing.jonli.adapters.ScooterArrayAdapter
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentScooterListBinding
import dk.itu.moapd.scootersharing.jonli.models.Scooter
import dk.itu.moapd.scootersharing.jonli.services.LocationService
import dk.itu.moapd.scootersharing.jonli.utils.LocationReceiver
import dk.itu.moapd.scootersharing.jonli.viewmodels.ScooterListViewModel
import dk.itu.moapd.scootersharing.jonli.viewmodels.ScooterListViewModelFactory

class ScooterListFragment : BaseFragment() {

    private lateinit var binding: FragmentScooterListBinding
    private lateinit var viewModel: ScooterListViewModel
    private lateinit var locationReceiver: LocationReceiver
    private lateinit var adapter: ScooterArrayAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentScooterListBinding.inflate(layoutInflater, container, false)

        val viewModelFactory = ScooterListViewModelFactory(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[ScooterListViewModel::class.java]

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        requestLocationPermission()

        locationReceiver = LocationReceiver(viewModel)

        setupRecycler()
        setupListeners()
        return binding.root
    }

    private fun setupRecycler() {
        viewModel.auth.currentUser?.let {
            val query = viewModel.database.child("scooters").orderByChild("available").equalTo(true)

            val options = FirebaseRecyclerOptions.Builder<Scooter>()
                .setQuery(query, Scooter::class.java)
                .setLifecycleOwner(this)
                .build()

            adapter = ScooterArrayAdapter(options) { scooter ->
                findNavController()
                    .navigate(
                        ScooterListFragmentDirections.actionScooterListFragmentToScooterDetailsFragment(scooter, null),
                    )
            }
            binding.recyclerView.adapter = adapter
        }
    }

    private fun setupListeners() {
        binding.findNearestScooter.setOnClickListener {
            if (checkPermission()) {
                return@setOnClickListener
            }
            val scooterId = viewModel.getNearestScooters()
            scooterId?.let {
                findNavController()
                    .navigate(
                        ScooterListFragmentDirections
                            .actionScooterListFragmentToScooterDetailsFragment(scooterId, null),
                    )
            }
        }
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.scannerFragment -> {
                    findNavController()
                        .navigate(ScooterListFragmentDirections.actionScooterListFragmentToScannerFragment(null))
                    true
                }
                else -> false
            }
        }
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
    }

    override fun onPause() {
        super.onPause()
        Intent(requireContext(), LocationService::class.java).also {
            requireContext().stopService(it)
        }
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(locationReceiver)
    }
}
