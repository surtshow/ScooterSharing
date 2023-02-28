package dk.itu.moapd.scootersharing.jonli

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    companion object {
        lateinit var ridesDB: RidesDB
        private lateinit var adapter: CustomArrayAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ridesDB = RidesDB.get(this.requireContext())
        adapter = CustomArrayAdapter(this.requireContext(), R.layout.list_rides, ridesDB.getRidesList())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        binding.listView.adapter = adapter
        return binding.root
    }

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
