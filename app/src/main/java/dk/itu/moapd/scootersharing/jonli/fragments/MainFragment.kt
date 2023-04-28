package dk.itu.moapd.scootersharing.jonli.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.jonli.R
import dk.itu.moapd.scootersharing.jonli.activities.LoginActivity
import dk.itu.moapd.scootersharing.jonli.adapters.ScooterArrayAdapter
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentMainBinding
import dk.itu.moapd.scootersharing.jonli.models.Scooter

class MainFragment : Fragment() {

    val DATABASE_URL =
        "https://scooter-sharing-b2ed6-default-rtdb.europe-west1.firebasedatabase.app/"

    private lateinit var binding: FragmentMainBinding

    private lateinit var auth: FirebaseAuth

    companion object {
        fun dataChanged() {
            adapter.notifyDataSetChanged()
        }

        lateinit var database: DatabaseReference
        private lateinit var adapter: ScooterArrayAdapter
        lateinit var scooterToDelete: Scooter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = Firebase.database(DATABASE_URL).reference
        // database.keepSynced(true)

        auth.currentUser?.let {
            val query = database.child("scooters")
                .child(it.uid)
            val options = FirebaseRecyclerOptions.Builder<Scooter>()
                .setQuery(query, Scooter::class.java)
                .setLifecycleOwner(this)
                .build()

            adapter = ScooterArrayAdapter(
                options,
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    /**
     * Called immediately after onCreateView() has returned,
     * but before any saved state has been restored in to the view.
     *
     * Sets up listeners for each of the buttons on the MainFragment page.
     *
     * @param view The View returned by onCreateView().
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     * saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            startRideButton.setOnClickListener {
                findNavController()
                    .navigate(R.id.action_mainFragment_to_startRideFragment)
            }

            updateRideButton.setOnClickListener {
                findNavController()
                    .navigate(R.id.action_mainFragment_to_updateRideFragment)
            }

            listRidesButton.setOnClickListener {
                adapter.notifyDataSetChanged()
            }

            logoutButton.setOnClickListener {
                auth.signOut()
                startLoginActivity()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            startLoginActivity()
        }
    }

    private fun startLoginActivity() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
    }
}
