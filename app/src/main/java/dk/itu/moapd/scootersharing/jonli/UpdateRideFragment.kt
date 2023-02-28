package dk.itu.moapd.scootersharing.jonli

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentUpdateRideBinding

class UpdateRideFragment : Fragment() {

    private lateinit var binding: FragmentUpdateRideBinding

    // private val scooter: Scooter = Scooter("", "")

    companion object {
        lateinit var ridesDB: RidesDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentUpdateRideBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            editTextName.setText(ridesDB.getCurrentScooter().name)
            val scooterLocation = editTextLocation

            updateRideButton.setOnClickListener {
                if (scooterLocation.text.isNotEmpty()
                ) {
                    val location = scooterLocation.text.toString().trim()
                    ridesDB.updateCurrentScooter(location)

                    scooterLocation.text.clear()
                    showMessage()
                }
            }
        }
    }

    private fun showMessage() {
        val scooter = StartRideFragment.ridesDB.getCurrentScooter()
        val message = "Ride started using Scooter(name=${scooter.name}, location=${scooter.location})."
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG,
        ).show()
    }
}
