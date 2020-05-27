package ru.rutoken.demoshift.ui.pin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import ru.rutoken.demoshift.databinding.FragmentPinBinding
import ru.rutoken.demoshift.utils.showError

class PinFragment : DialogFragment() {
    private lateinit var binding: FragmentPinBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: PinViewModel by viewModels()

        binding = FragmentPinBinding.inflate(inflater)
        binding.proceedButton.setOnClickListener { viewModel.login() }
        binding.showErrorButton.setOnClickListener {
            showError(binding.showErrorButton, "error occurred!")
        }

        viewModel.loginProceed.observe(
            viewLifecycleOwner,
            Observer { findNavController().navigate(PinFragmentDirections.toSignResultFragment()) }
        )

        viewModel.loginError.observe(
            viewLifecycleOwner,
            Observer { exception -> showError(binding.proceedButton, exception) }
        )

        return binding.root
    }
}
