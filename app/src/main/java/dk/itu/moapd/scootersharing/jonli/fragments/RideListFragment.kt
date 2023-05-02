package dk.itu.moapd.scootersharing.jonli.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.scootersharing.jonli.adapters.RideArrayAdapter
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentRideListBinding
import dk.itu.moapd.scootersharing.jonli.models.Ride
import dk.itu.moapd.scootersharing.jonli.viewmodels.RideListViewModel
import dk.itu.moapd.scootersharing.jonli.viewmodels.RideListViewModelFactory

class RideListFragment : Fragment() {

    private lateinit var binding: FragmentRideListBinding
    private lateinit var viewModel: RideListViewModel
    private lateinit var adapter: RideArrayAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRideListBinding.inflate(layoutInflater, container, false)

        val viewModelFactory = RideListViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory)[RideListViewModel::class.java]

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.auth.currentUser?.let {
            val query = viewModel.database.child("rides")
                .child(it.uid)

            val options = FirebaseRecyclerOptions.Builder<Ride>()
                .setQuery(query, Ride::class.java)
                .setLifecycleOwner(this)
                .build()

            adapter = RideArrayAdapter(
                options,
            )
        }
        binding.recyclerView.adapter = adapter

        return binding.root
    }
}
