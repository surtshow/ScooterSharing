package dk.itu.moapd.scootersharing.jonli.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.scootersharing.jonli.adapters.ScooterArrayAdapter
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentScooterListBinding
import dk.itu.moapd.scootersharing.jonli.models.Scooter
import dk.itu.moapd.scootersharing.jonli.viewmodels.ScooterListViewModel
import dk.itu.moapd.scootersharing.jonli.viewmodels.ScooterListViewModelFactory

class ScooterListFragment : Fragment() {

    private lateinit var binding: FragmentScooterListBinding
    private lateinit var viewModel: ScooterListViewModel
    private lateinit var adapter: ScooterArrayAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentScooterListBinding.inflate(layoutInflater, container, false)

        val viewModelFactory = ScooterListViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory)[ScooterListViewModel::class.java]

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.auth.currentUser?.let {
            val query = viewModel.database.child("scooters")
                .child(it.uid)

            val options = FirebaseRecyclerOptions.Builder<Scooter>()
                .setQuery(query, Scooter::class.java)
                .setLifecycleOwner(this)
                .build()

            adapter = ScooterArrayAdapter(
                options,
            ) { key ->
                findNavController()
                    .navigate(ScooterListFragmentDirections.actionScooterListFragmentToScooterDetailsFragment(key))
            }
        }
        binding.recyclerView.adapter = adapter

        return binding.root
    }

//    private fun setupObservers() {
//        viewModel.scooters.observe(viewLifecycleOwner) {
//            it?.let {
//                binding.recyclerView.adapter = ScooterArrayAdapter(it)
//            }
//        }
//    }
}
