package dk.itu.moapd.scootersharing.jonli

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.core.view.WindowCompat
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.jonli.databinding.ActivityUpdateRideBinding

class UpdateRideActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateRideBinding

    // private val scooter: Scooter = Scooter("", "")

    companion object {
        lateinit var ridesDB: RidesDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        ridesDB = RidesDB.get(this)
        binding = ActivityUpdateRideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editTextName.setText(ridesDB.getCurrentScooter().name)
        val scooterLocation = binding.editTextLocation

        binding.updateRideButton.setOnClickListener {
            if (scooterLocation.text.isNotEmpty()
            ) {
                val location = scooterLocation.text.toString().trim()
                ridesDB.updateCurrentScooter(location)

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
