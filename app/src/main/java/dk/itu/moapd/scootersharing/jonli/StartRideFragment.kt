package dk.itu.moapd.scootersharing.jonli

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentStartRideBinding

class StartRideFragment : Fragment() {

    private lateinit var binding: FragmentStartRideBinding

    // private val scooter: Scooter = Scooter("", "")

    companion object {
        lateinit var ridesDB: RidesDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ridesDB = RidesDB.get(this.requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentStartRideBinding.inflate(layoutInflater, container, false)
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
                    ridesDB.addScooter(name, location)

                    editTextName.text.clear()
                    editTextLocation.text.clear()
                    showMessage()
                }
            }
        }
    }

    private fun showMessage() {
        val scooter = ridesDB.getCurrentScooter()
        val message = "Ride started using Scooter(name=${scooter.name}, at location=${scooter.location})."
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG,
        ).show()
    }
}
