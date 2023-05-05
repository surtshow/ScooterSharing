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
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.scootersharing.jonli.adapters.RideArrayAdapter
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentRideListBinding
import dk.itu.moapd.scootersharing.jonli.enumerators.RideStatus
import dk.itu.moapd.scootersharing.jonli.models.Ride
import dk.itu.moapd.scootersharing.jonli.viewmodels.RideListViewModel
import dk.itu.moapd.scootersharing.jonli.viewmodels.RideListViewModelFactory

class RideListFragment : Fragment() {

    private lateinit var binding: FragmentRideListBinding
    private lateinit var viewModel: RideListViewModel
    private lateinit var query: com.google.firebase.database.Query

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

        setupRecycler()
        setupListeners()
        return binding.root
    }

    private fun setupRecycler() {
        viewModel.auth.currentUser?.let {
            when (args.status) {
                RideStatus.STARTED -> {
                    query = viewModel.database.child("rides")
                        .child(it.uid)
                        .orderByChild("status").equalTo("STARTED")
                }
                RideStatus.ENDED -> {
                    query = viewModel.database.child("rides")
                        .child(it.uid)
                        .orderByChild("status").equalTo("ENDED")
                }
                RideStatus.RESERVED -> {
                    query = viewModel.database.child("rides")
                        .child(it.uid)
                        .orderByChild("status").equalTo("RESERVED")
                }
                else -> {}
            }

            val options = FirebaseRecyclerOptions.Builder<Ride>()
                .setQuery(query, Ride::class.java)
                .setLifecycleOwner(this)
                .build()

            when (args.status) {
                RideStatus.STARTED, RideStatus.RESERVED -> {
                    binding.recyclerView.adapter = RideArrayAdapter(options) { key, ride ->
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
                    binding.recyclerView.adapter = RideArrayAdapter(options)
                }
                else -> {}
            }
        }
    }

    fun setupListeners() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}
