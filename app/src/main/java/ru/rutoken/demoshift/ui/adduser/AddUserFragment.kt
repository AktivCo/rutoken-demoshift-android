/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.adduser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.databinding.FragmentAddUserBinding
import ru.rutoken.demoshift.databinding.WorkProgressBinding
import ru.rutoken.demoshift.ui.adduser.AddUserFragmentDirections.toUserListFragment
import ru.rutoken.demoshift.utils.asReadableText

class AddUserFragment : Fragment() {
    private lateinit var workProgressBinding: WorkProgressBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddUserBinding.inflate(inflater)
        workProgressBinding = binding.addUserWorkProgress

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: AddUserFragmentArgs by navArgs()
        val viewModel: AddUserViewModel = getViewModel(parameters = { parametersOf(args.pin) })

        viewModel.status.observe(viewLifecycleOwner, Observer { status ->
            status.message?.let { workProgressBinding.statusTextView.text = it }

            workProgressBinding.progressBar.visibility =
                if (status.isProgress) View.VISIBLE else View.GONE
        })

        viewModel.result.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                Toast.makeText(context, getString(R.string.add_user_ok), Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(toUserListFragment())
            } else {
                val exceptionMessage = (result.exceptionOrNull() as Exception).message.orEmpty()
                workProgressBinding.statusTextView.text =
                    result.exceptionOrNull()?.asReadableText(requireContext())
                        ?: "${getString(R.string.error_text)}\n\n" + exceptionMessage
            }
        })
    }
}