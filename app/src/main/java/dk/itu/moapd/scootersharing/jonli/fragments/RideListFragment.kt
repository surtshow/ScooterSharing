package dk.itu.moapd.scootersharing.jonli.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dk.itu.moapd.scootersharing.jonli.adapters.RideArrayAdapter
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentRideListBinding
import dk.itu.moapd.scootersharing.jonli.enumerators.RideStatus
import dk.itu.moapd.scootersharing.jonli.viewmodels.RideListViewModel
import dk.itu.moapd.scootersharing.jonli.viewmodels.RideListViewModelFactory

class RideListFragment : Fragment() {

    private lateinit var binding: FragmentRideListBinding
    private lateinit var viewModel: RideListViewModel

    private val args: RideListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRideListBinding.inflate(layoutInflater, container, false)

        val viewModelFactory = RideListViewModelFactory(this, args.status)
        viewModel = ViewModelProvider(this, viewModelFactory)[RideListViewModel::class.java]

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        setupObservers()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.rides.observe(viewLifecycleOwner) {
            it?.let {
                when (args.status) {
                    RideStatus.STARTED -> {
                        binding.recyclerView.adapter = RideArrayAdapter(it) { key, ride ->
                            findNavController()
                                .navigate(
                                    RideListFragmentDirections.actionRideListFragmentToScooterDetailsFragment(
                                        ride.scooterId!!,
                                        key,
                                    ),
                                )
                        }
                    }
                    RideStatus.ENDED -> {
                        binding.recyclerView.adapter = RideArrayAdapter(it) { _, ride ->
                            findNavController()
                                .navigate(
                                    RideListFragmentDirections.actionRideListFragmentToScooterDetailsFragment(
                                        ride.scooterId!!,
                                        null,
                                    ),
                                )
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}
