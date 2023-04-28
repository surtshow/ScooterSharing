package dk.itu.moapd.scootersharing.jonli.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import dk.itu.moapd.scootersharing.jonli.RidesDB
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentStartRideBinding
import dk.itu.moapd.scootersharing.jonli.models.Scooter
import dk.itu.moapd.scootersharing.jonli.viewmodels.StartRideViewModel
import dk.itu.moapd.scootersharing.jonli.viewmodels.StartRideViewModelFactory

class StartRideFragment : Fragment() {

    private lateinit var binding: FragmentStartRideBinding
    private lateinit var viewModel: StartRideViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    companion object {
        lateinit var ridesDB: RidesDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = MainFragment.database
        ridesDB = RidesDB.get(this.requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStartRideBinding.inflate(layoutInflater, container, false)

        val viewModelFactory = StartRideViewModelFactory(auth, database)
        viewModel = ViewModelProvider(this, viewModelFactory)[StartRideViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            startRideButton.setOnClickListener {
                if (editTextName.text.isNotEmpty() &&
                    editTextLocation.text.isNotEmpty()
                ) {
                    val name = editTextName.text.toString().trim()
                    val location = editTextLocation.text.toString().trim()
                    val scooter = viewModel.createRide(name, location)

                    editTextName.text.clear()
                    editTextLocation.text.clear()
                    showMessage(scooter)
                }
            }
        }
    }

    private fun showMessage(scooter: Scooter) {
        val message = "Ride started using Scooter(name=${scooter.name}, at location=${scooter.location})."
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG,
        ).show()
    }
}
