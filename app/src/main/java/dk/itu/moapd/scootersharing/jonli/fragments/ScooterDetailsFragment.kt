package dk.itu.moapd.scootersharing.jonli.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentScooterDetailsBinding
import dk.itu.moapd.scootersharing.jonli.viewmodels.ScooterDetailsViewModel
import dk.itu.moapd.scootersharing.jonli.viewmodels.ScooterDetailsViewModelFactory

class ScooterDetailsFragment : Fragment() {

    private lateinit var binding: FragmentScooterDetailsBinding
    private lateinit var viewModel: ScooterDetailsViewModel

    private val args: ScooterDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentScooterDetailsBinding.inflate(layoutInflater, container, false)

        val viewModelFactory = ScooterDetailsViewModelFactory(args.scooterId)
        viewModel = ViewModelProvider(this, viewModelFactory)[ScooterDetailsViewModel::class.java]

        setupObservers()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.scooter.observe(viewLifecycleOwner) {
            it?.let {
                binding.scooterName.text = it.name
            }
        }
    }
}
