package dk.itu.moapd.scootersharing.jonli.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentScooterListBinding

class ScooterListFragment : Fragment() {

    private lateinit var binding: FragmentScooterListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentScooterListBinding.inflate(layoutInflater, container, false)

        return binding.root
    }
}
