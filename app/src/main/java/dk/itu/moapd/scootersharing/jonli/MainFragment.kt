package dk.itu.moapd.scootersharing.jonli

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

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
            DeleteScooterFragment
                .newInstance(scooter.name)
                .show(parentFragmentManager, DeleteScooterFragment.TAG)
            true
        }
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
        }
    }
}
