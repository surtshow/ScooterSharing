package dk.itu.moapd.scootersharing.jonli.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.jonli.R
import dk.itu.moapd.scootersharing.jonli.RidesDB
import dk.itu.moapd.scootersharing.jonli.activities.LoginActivity
import dk.itu.moapd.scootersharing.jonli.adapters.CustomArrayAdapter
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentMainBinding
import dk.itu.moapd.scootersharing.jonli.models.Scooter

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private lateinit var auth: FirebaseAuth

    companion object {
        fun dataChanged() {
            adapter.notifyDataSetChanged()
        }

        lateinit var ridesDB: RidesDB
        private lateinit var adapter: CustomArrayAdapter
        lateinit var scooterToDelete: Scooter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ridesDB = RidesDB.get(this.requireContext())
        adapter = CustomArrayAdapter(ridesDB.getRidesList()) { scooter ->
            scooterToDelete = scooter
            DeleteScooterFragment.newInstance(scooter.name)
                .show(parentFragmentManager, DeleteScooterFragment.TAG)
            true
        }
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        binding.recyclerView?.layoutManager = LinearLayoutManager(context)
        binding.recyclerView?.adapter = adapter
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
