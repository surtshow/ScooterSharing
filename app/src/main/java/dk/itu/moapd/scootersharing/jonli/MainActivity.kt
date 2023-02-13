package dk.itu.moapd.scootersharing.jonli

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.jonli.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }

    private lateinit var binding: ActivityMainBinding

    private val scooter: Scooter = Scooter("", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val scooterName = binding.editTextName
        val scooterLocation = binding.editTextLocation

        binding.startRideButton.setOnClickListener {
            if (scooterName.text.isNotEmpty() &&
                scooterLocation.text.isNotEmpty()
            ) {
                val name = scooterName.text.toString().trim()
                val location = scooterLocation.text.toString().trim()
                scooter.name = name
                scooter.location = location

                scooterName.text.clear()
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
