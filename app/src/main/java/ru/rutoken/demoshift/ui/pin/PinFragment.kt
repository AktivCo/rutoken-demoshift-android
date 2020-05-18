package ru.rutoken.demoshift.ui.pin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ru.rutoken.demoshift.databinding.FragmentPinBinding

class PinFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPinBinding.inflate(inflater)
        return binding.root
    }
}