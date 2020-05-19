package ru.rutoken.demoshift.ui.sign

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.databinding.FragmentSignResultBinding


class SignResultFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignResultBinding.inflate(inflater)
        val viewModel: SignViewModel by viewModels()

        viewModel.getSignResult().observe(viewLifecycleOwner, Observer {
            binding.signResultTextView.text = it
        })

        binding.signResultTextView.setOnLongClickListener {
            val manager =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            manager.setPrimaryClip(ClipData.newPlainText("CMS", binding.signResultTextView.text))

            Snackbar.make(binding.signResultTextView, R.string.copied_text, Snackbar.LENGTH_SHORT)
                .setAnchorView(binding.backToUserListButton)
                .show()

            true
        }

        binding.backToUserListButton.setOnClickListener {
            findNavController().navigate(SignResultFragmentDirections.toUserListFragment())
        }

        return binding.root
    }
}
