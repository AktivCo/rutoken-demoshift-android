package ru.rutoken.demoshift.ui.sign

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import ru.rutoken.demoshift.databinding.FragmentSignBinding
import ru.rutoken.demoshift.ui.pin.PinDialogFragment
import ru.rutoken.demoshift.ui.pin.PinDialogFragment.Companion.DIALOG_RESULT_KEY
import ru.rutoken.demoshift.ui.pin.PinDialogFragment.Companion.PIN_KEY
import ru.rutoken.demoshift.ui.sign.SignFragmentDirections.toSignResultFragment
import ru.rutoken.demoshift.utils.createFileSharingIntent


class SignFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.setFragmentResultListener(DIALOG_RESULT_KEY, this) { _, bundle ->
            val pin = bundle.getString(PIN_KEY)
            findNavController().navigate(toSignResultFragment())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignBinding.inflate(inflater)
        val signDocument = "sign_document.pdf"
        binding.signPdfView.fromAsset(signDocument)
            .scrollHandle(DefaultScrollHandle(requireContext()))
            .onLongPress { startActivity(createFileSharingIntent(signDocument, requireContext())) }
            .load()

        binding.signButton.setOnClickListener {
            PinDialogFragment().show(childFragmentManager, null)
        }

        return binding.root
    }
}
