package dk.itu.moapd.scootersharing.jonli.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentScooterDetailsBinding
import dk.itu.moapd.scootersharing.jonli.viewmodels.ScooterDetailsViewModel

class ScooterDetailsFragment : Fragment() {

    private lateinit var binding: FragmentScooterDetailsBinding
    private lateinit var viewModel: ScooterDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentScooterDetailsBinding.inflate(layoutInflater, container, false)

        return binding.root
    }
}
