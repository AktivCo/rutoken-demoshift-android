package ru.rutoken.demoshift.ui.signresult

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.databinding.FragmentSignResultBinding
import ru.rutoken.demoshift.ui.signresult.SignResultFragmentDirections.toUserListFragment


class SignResultFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignResultBinding.inflate(inflater)
        val args: SignResultFragmentArgs by navArgs()
        binding.signResultTextView.text = args.signature

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
            findNavController().navigate(toUserListFragment())
        }

        return binding.root
    }
}