/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.certificatelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.databinding.CertificateCardBinding
import ru.rutoken.demoshift.utils.getAlgorithmNameById


class CertificateListAdapter(private val listener: CertificateCardListener) :
    RecyclerView.Adapter<CertificateListAdapter.CertificateViewHolder>() {
    var certificates: List<Certificate> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: CertificateViewHolder, position: Int) {
        val view = holder.view
        val certificate = getCertificate(position)
        val binding = CertificateCardBinding.bind(view)

        binding.userFullName.text = certificate.fullName
        binding.userPosition.text =
            certificate.position ?: view.context.getString(R.string.field_not_set)
        binding.userOrganization.text =
            certificate.organization ?: view.context.getString(R.string.field_not_set)
        binding.algorithm.text = getAlgorithmNameById(view.context, certificate.algorithmId)
            ?: view.context.getString(R.string.field_not_set)
        binding.userCertificateExpires.text = certificate.certificateExpires
        binding.inn.text =
            certificate.inn ?: view.context.getString(R.string.field_not_set)
        binding.ogrn.text = certificate.ogrn ?: view.context.getString(R.string.field_not_set)
        binding.certificateView.setOnClickListener {
            listener.onClick(certificate)
        }
    }

    private fun getCertificate(position: Int) = certificates[position]

    override fun getItemCount() = certificates.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateViewHolder {
        val certificateView =
            CertificateCardBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
        return CertificateViewHolder(certificateView)
    }

    class CertificateViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}

interface CertificateCardListener {
    fun onClick(certificate: Certificate)
}
