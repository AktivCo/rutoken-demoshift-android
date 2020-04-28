package ru.rutoken.demoshift.ui.adduser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.rutoken.demoshift.databinding.FragmentAddUserBinding

class AddUserFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddUserBinding.inflate(inflater)
        return binding.root
    }
}