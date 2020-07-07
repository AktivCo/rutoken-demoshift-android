/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.certificatelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.databinding.FragmentCertificateListBinding
import ru.rutoken.demoshift.ui.certificatelist.CertificateListFragmentDirections.toUserListFragment
import ru.rutoken.demoshift.ui.workprogress.WorkProgressView.Status
import ru.rutoken.demoshift.utils.asReadableText
import ru.rutoken.demoshift.utils.showError


class CertificateListFragment : Fragment() {
    private lateinit var binding: FragmentCertificateListBinding
    private lateinit var viewModel: CertificateListViewModel

    private val certificatesListAdapter = CertificateListAdapter(object : CertificateCardListener {
        override fun onClick(certificate: Certificate) {
            viewModel.addUser(certificate)
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCertificateListBinding.inflate(inflater)
        binding.certificatesRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = certificatesListAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: CertificateListFragmentArgs by navArgs()
        viewModel = getViewModel(parameters = { parametersOf(args.pin) })

        viewModel.status.observe(viewLifecycleOwner, Observer {
            binding.workProgress.setStatus(it)
        })

        viewModel.pkcs11Result.observe(viewLifecycleOwner, Observer { result ->
            binding.certificatesView.visibility = if (result.isSuccess) VISIBLE else GONE
            binding.workProgress.visibility = if (result.isSuccess) GONE else VISIBLE

            if (result.isSuccess)
                certificatesListAdapter.certificates = result.getOrThrow()
            else
                binding.workProgress.setStatus(Status(result.getFailureMessage()))
        })

        viewModel.addUserResult.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                Toast.makeText(context, getString(R.string.add_user_ok), Toast.LENGTH_SHORT).show()
                findNavController().navigate(toUserListFragment())
            } else {
                showError(binding.certificatesRecyclerView, result.getFailureMessage())
            }
        })
    }

    private fun <T> Result<T>.getFailureMessage() =
        exceptionOrNull()?.asReadableText(requireContext())
            ?: "${getString(R.string.error_text)}\n\n" +
            (exceptionOrNull() as Exception).message.orEmpty()
}