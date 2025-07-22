/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.document

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.getViewModel
import ru.rutoken.demoshift.databinding.FragmentDocumentBinding
import ru.rutoken.demoshift.ui.applyTopInset
import ru.rutoken.demoshift.ui.document.DocumentFragmentDirections.toSignFragment
import ru.rutoken.demoshift.ui.pin.PinDialogFragment
import ru.rutoken.demoshift.ui.pin.PinDialogFragment.Companion.DIALOG_RESULT_KEY

class DocumentFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentDocumentBinding.inflate(inflater)
        val args: DocumentFragmentArgs by navArgs()

        val viewModel = getViewModel<DocumentViewModel>()

        val getContentLauncher = registerForActivityResult(GetContent()) { uri: Uri? ->
            uri?.let { viewModel.updateDocument(it) }
        }

        binding.toolbarLayout.toolbar.applyTopInset()

        viewModel.isDocumentPdf.observe(viewLifecycleOwner) { isSupported ->
            if (isSupported) viewModel.renderPdfDocument(resources.displayMetrics.densityDpi)

            binding.documentPdfRecyclerView.visibility = if (isSupported) VISIBLE else GONE
            binding.unsupportedFileSelected.visibility = if (isSupported) GONE else VISIBLE
        }

        viewModel.documentPdfBitmaps.observe(viewLifecycleOwner) { bitmaps ->
            DocumentPdfAdapter().also { adapter ->
                binding.documentPdfRecyclerView.adapter = adapter
                bitmaps.forEach { adapter.addPdfPageBitmap(it) }
            }
        }

        viewModel.navigationArguments.observe(viewLifecycleOwner) { navArgs ->
            findNavController().navigate(toSignFragment(navArgs.pin, args.userId, navArgs.documentUri))
        }

        binding.signButton.setOnClickListener {
            PinDialogFragment().show(childFragmentManager, null)
        }

        binding.selectButton.setOnClickListener {
            getContentLauncher.launch("*/*")
        }

        childFragmentManager.setFragmentResultListener(DIALOG_RESULT_KEY, this) { _, bundle ->
            viewModel.handleFragmentResult(bundle)
        }

        return binding.root
    }
}
