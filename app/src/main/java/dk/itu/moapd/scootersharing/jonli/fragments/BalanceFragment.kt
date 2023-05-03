package dk.itu.moapd.scootersharing.jonli.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dk.itu.moapd.scootersharing.jonli.databinding.FragmentBalanceBinding
import dk.itu.moapd.scootersharing.jonli.viewmodels.BalanceViewModel
import dk.itu.moapd.scootersharing.jonli.viewmodels.BalanceViewModelFactory

class BalanceFragment : BaseFragment() {

    private lateinit var binding: FragmentBalanceBinding
    private lateinit var viewModel: BalanceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBalanceBinding.inflate(layoutInflater, container, false)

        val viewModelFactory = BalanceViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory)[BalanceViewModel::class.java]

        setupListeners()
        setupObservers()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.balance.observe(viewLifecycleOwner) {
            binding.currentBalanceTextView.text = it.toString() ?: "0.0"
        }
    }

    private fun setupListeners() {
        binding.addFundsButton.setOnClickListener {
            val funds = binding.balanceEditText.text.toString().toDoubleOrNull()

            if (funds != null) {
                viewModel.updateBalance(funds)
            } else {
                // TODO: Show error message
            }
            binding.balanceEditText.text.clear()
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}
