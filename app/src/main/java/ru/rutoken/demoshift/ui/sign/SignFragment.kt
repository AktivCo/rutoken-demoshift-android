/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.sign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.databinding.FragmentSignBinding
import ru.rutoken.demoshift.ui.sign.SignFragmentDirections.toSignResultFragment
import ru.rutoken.demoshift.ui.workprogress.WorkProgressView.Status
import ru.rutoken.demoshift.utils.asReadableText

class SignFragment : Fragment() {
    private lateinit var binding: FragmentSignBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: SignFragmentArgs by navArgs()
        val viewModel: SignViewModel =
            getViewModel(parameters = { parametersOf(args.pin, args.documentUri, args.userId) })


        viewModel.status.observe(viewLifecycleOwner) {
            binding.workProgress.setStatus(it)
        }

        viewModel.result.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                findNavController().navigate(
                    toSignResultFragment(
                        args.documentUri,
                        result.getOrThrow()
                    )
                )
            } else {
                val exceptionMessage = (result.exceptionOrNull() as Exception).message.orEmpty()
                binding.workProgress.setStatus(
                    Status(
                        result.exceptionOrNull()?.asReadableText(requireContext())
                            ?: "${getString(R.string.error_text)}\n\n" + exceptionMessage
                    )
                )
            }
        }
    }
}
