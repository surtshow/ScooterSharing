package dk.itu.moapd.scootersharing.jonli.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dk.itu.moapd.scootersharing.jonli.R
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentProfileBinding
import dk.itu.moapd.scootersharing.jonli.enumerators.RideStatus

class ProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.activeRides.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToRideListFragment(RideStatus.STARTED))
        }

        binding.finishedRides.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToRideListFragment(RideStatus.ENDED))
        }

        binding.checkBalance.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToBalanceFragment())
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.scannerFragment -> {
                    findNavController()
                        .navigate(ProfileFragmentDirections.actionProfileFragmentToScannerFragment(null))
                    true
                }
                else -> false
            }
        }
    }
}
