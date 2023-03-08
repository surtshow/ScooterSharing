package dk.itu.moapd.scootersharing.jonli

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dk.itu.moapd.scootersharing.jonli.databinding.ActivityMainBinding

/**
 * The main activity of the application.
 */
class MainActivity : AppCompatActivity() {

    /**
     * The binding object instance which is used to access
     * the views that are defined in the layout file.
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized
     * after previously being shut down then this Bundle contains the data
     * it most recently supplied in onSaveInstanceState(Bundle).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
