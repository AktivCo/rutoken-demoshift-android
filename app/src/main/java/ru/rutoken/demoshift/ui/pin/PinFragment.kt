package ru.rutoken.demoshift.ui.pin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import ru.rutoken.demoshift.databinding.FragmentPinBinding
import ru.rutoken.demoshift.utils.showError

class PinFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPinBinding.inflate(inflater)

        binding.proceedButton.setOnClickListener {
            findNavController().navigate(PinFragmentDirections.toSignResultFragment())
        }

        binding.showErrorButton.setOnClickListener {
            showError(binding.showErrorButton, "error occurred!")
        }

        return binding.root
    }
}