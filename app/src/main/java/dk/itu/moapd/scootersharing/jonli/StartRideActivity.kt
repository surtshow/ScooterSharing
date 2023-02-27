package dk.itu.moapd.scootersharing.jonli

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.jonli.databinding.ActivityStartRideBinding

class StartRideActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartRideBinding

    // private val scooter: Scooter = Scooter("", "")

    companion object {
        lateinit var ridesDB: RidesDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        ridesDB = RidesDB.get(this)
        binding = ActivityStartRideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val scooterName = binding.editTextName
        val scooterLocation = binding.editTextLocation

        binding.startRideButton.setOnClickListener {
            if (scooterName.text.isNotEmpty() &&
                scooterLocation.text.isNotEmpty()
            ) {
                val name = scooterName.text.toString().trim()
                val location = scooterLocation.text.toString().trim()
                ridesDB.addScooter(name, location)

                scooterName.text.clear()
                scooterLocation.text.clear()
                showMessage()
            }
        }
    }

    private fun showMessage() {
        val scooter = ridesDB.getCurrentScooter()
        val message = "Ride started using Scooter(name=${scooter.name}, location=${scooter.location})."
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG,
        ).show()
    }
}
