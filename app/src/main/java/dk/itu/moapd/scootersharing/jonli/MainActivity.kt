package dk.itu.moapd.scootersharing.jonli

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import dk.itu.moapd.scootersharing.jonli.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        lateinit var ridesDB: RidesDB
        private lateinit var adapter: CustomArrayAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        ridesDB = RidesDB.get(this)
        adapter = CustomArrayAdapter(this, R.layout.list_rides, ridesDB.getRidesList())
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.listView.adapter = adapter
        setContentView(binding.root)

        binding.startRideButton.setOnClickListener {
            val intent = Intent(this, StartRideActivity::class.java)
            startActivity(intent)
        }
        binding.updateRideButton.setOnClickListener {
            val intent = Intent(this, UpdateRideActivity::class.java)
            startActivity(intent)
        }
        binding.listRidesButton.setOnClickListener {
            adapter.notifyDataSetChanged()
        }
    }
}
