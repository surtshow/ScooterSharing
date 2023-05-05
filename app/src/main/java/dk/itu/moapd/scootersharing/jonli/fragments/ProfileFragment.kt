package dk.itu.moapd.scootersharing.jonli.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.UserProfileChangeRequest
import dk.itu.moapd.scootersharing.jonli.R
import dk.itu.moapd.scootersharing.jonli.activities.LoginActivity
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentProfileBinding
import dk.itu.moapd.scootersharing.jonli.enumerators.RideStatus
import dk.itu.moapd.scootersharing.jonli.viewmodels.ProfileViewModel
import dk.itu.moapd.scootersharing.jonli.viewmodels.ProfileViewModelFactory

class ProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        val viewModelFactory = ProfileViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]

        requestLocationPermission()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestSensorPermission()
        }

        viewModel.auth.currentUser?.displayName?.let {
            binding.name.text = it
        }

        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.updateNameButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            if (name.isNotEmpty()) {
                viewModel.auth.currentUser?.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build(),
                )?.addOnSuccessListener {
                    binding.name.text = viewModel.auth.currentUser?.displayName
                    binding.nameEditText.text.clear()
                }
            }
        }
        binding.activeRides.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToRideListFragment(RideStatus.STARTED),
            )
        }

        binding.finishedRides.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToRideListFragment(RideStatus.ENDED),
            )
        }

        binding.reservedRides.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToRideListFragment(RideStatus.RESERVED),
            )
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
        binding.logoutButton.setOnClickListener {
            viewModel.auth.signOut()
            startLoginActivity()
        }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.auth.currentUser == null) {
            startLoginActivity()
        }
    }

    private fun startLoginActivity() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
    }
}
