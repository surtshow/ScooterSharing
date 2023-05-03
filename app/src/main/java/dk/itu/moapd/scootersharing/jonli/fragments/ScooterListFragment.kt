package dk.itu.moapd.scootersharing.jonli.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dk.itu.moapd.scootersharing.jonli.R
import dk.itu.moapd.scootersharing.jonli.adapters.ScooterArrayAdapter
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentScooterListBinding
import dk.itu.moapd.scootersharing.jonli.viewmodels.ScooterListViewModel
import dk.itu.moapd.scootersharing.jonli.viewmodels.ScooterListViewModelFactory

class ScooterListFragment : Fragment() {

    private lateinit var binding: FragmentScooterListBinding
    private lateinit var viewModel: ScooterListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentScooterListBinding.inflate(layoutInflater, container, false)

        val viewModelFactory = ScooterListViewModelFactory(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[ScooterListViewModel::class.java]

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        setupObservers()
        setupListeners()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.scooters.observe(viewLifecycleOwner) {
            it?.let {
                binding.recyclerView.adapter = ScooterArrayAdapter(it) { scooter ->
                    findNavController()
                        .navigate(
                            ScooterListFragmentDirections.actionScooterListFragmentToScooterDetailsFragment(scooter, null),
                        )
                }
            }
        }
    }

    private fun setupListeners() {
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
}
