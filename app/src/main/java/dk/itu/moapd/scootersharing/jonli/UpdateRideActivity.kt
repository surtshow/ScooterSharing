package dk.itu.moapd.scootersharing.jonli

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.jonli.databinding.ActivityUpdateRideBinding

class UpdateRideActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateRideBinding

    private val scooter: Scooter = Scooter("", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateRideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val scooterLocation = binding.editTextLocation

        binding.updateRideButton.setOnClickListener {
            if (scooterLocation.text.isNotEmpty()
            ) {
                val location = scooterLocation.text.toString().trim()
                scooter.location = location

                scooterLocation.text.clear()
                showMessage()
            }
        }
    }

    private fun showMessage() {
        val message = "Ride started using Scooter(name=$scooter.name, location=$scooter.location)."
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }
}
