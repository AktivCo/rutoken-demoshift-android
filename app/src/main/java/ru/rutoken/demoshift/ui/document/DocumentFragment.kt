package ru.rutoken.demoshift.ui.document

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import ru.rutoken.demoshift.databinding.FragmentDocumentBinding
import ru.rutoken.demoshift.ui.document.DocumentFragmentDirections.toSignFragment
import ru.rutoken.demoshift.ui.pin.PinDialogFragment
import ru.rutoken.demoshift.ui.pin.PinDialogFragment.Companion.DIALOG_RESULT_KEY
import ru.rutoken.demoshift.ui.pin.PinDialogFragment.Companion.PIN_KEY
import ru.rutoken.demoshift.utils.createFileSharingIntent
import java.io.File


class DocumentFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentDocumentBinding.inflate(inflater)
        val args: DocumentFragmentArgs by navArgs()

        val document = "sign_document.pdf"
        getViewModel<DocumentViewModel>(parameters = { parametersOf(document) })

        val documentUri = FileProvider.getUriForFile(
            requireContext(),
            "ru.rutoken.demoshift.fileprovider",
            File(requireContext().cacheDir, "/$document")
        )

        binding.documentPdfView.fromAsset(document)
            .scrollHandle(DefaultScrollHandle(requireContext()))
            .onLongPress { startActivity(createFileSharingIntent(documentUri, requireContext())) }
            .load()

        binding.signButton.setOnClickListener {
            PinDialogFragment().show(childFragmentManager, null)
        }

        childFragmentManager.setFragmentResultListener(DIALOG_RESULT_KEY, this) { _, bundle ->
            findNavController().navigate(
                toSignFragment(
                    checkNotNull(bundle.getString(PIN_KEY)),
                    args.userId,
                    documentUri
                )
            )
        }

        return binding.root
    }
}