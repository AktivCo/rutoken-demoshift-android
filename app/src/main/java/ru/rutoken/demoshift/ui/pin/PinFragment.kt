package ru.rutoken.demoshift.ui.pin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.getViewModel
import ru.rutoken.demoshift.databinding.FragmentPinBinding
import ru.rutoken.demoshift.utils.showError

class PinFragment : DialogFragment() {
    private lateinit var binding: FragmentPinBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel = getViewModel<PinViewModel>()

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
