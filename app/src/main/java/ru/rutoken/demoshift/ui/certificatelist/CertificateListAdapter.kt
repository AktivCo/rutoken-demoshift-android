/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.certificatelist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.databinding.CertificateCardBinding
import ru.rutoken.demoshift.utils.getAlgorithmNameById

private const val NBSP = "\u00A0"

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

        fun String?.orNotSet() = this ?: view.context.getString(R.string.field_not_set)

        binding.userFullName.text = certificate.fullName
        binding.userPosition.text = certificate.position.orNotSet()
        binding.userOrganization.text = certificate.organization.orNotSet()
        binding.algorithm.text = getAlgorithmNameById(view.context, certificate.algorithmId).orNotSet()
        binding.userCertificateExpires.text = certificate.certificateExpires
        binding.inn.text = view.context.getInnText(certificate)
        binding.ogrn.isVisible = certificate.ogrn != null || certificate.ogrnip != null
        binding.ogrn.text = view.context.getOgrnText(certificate)
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

private fun Context.getInnText(certificate: Certificate) = buildSpannedString {
    bold { append(getString(R.string.inn)) }
    append(NBSP)
    append("${certificate.inn ?: getString(R.string.field_not_set)} ")
    bold { append(getString(R.string.innle)) }
    append(NBSP)
    append(certificate.innle ?: getString(R.string.field_not_set))
}

private fun Context.getOgrnText(certificate: Certificate) = buildSpannedString {
    if (certificate.ogrn != null) {
        bold { append(getString(R.string.ogrn)) }
        append(NBSP)
        append(certificate.ogrn)
        if (certificate.ogrnip != null)
            append(" ")
    }
    if (certificate.ogrnip != null) {
        bold { append(getString(R.string.ogrnip)) }
        append(NBSP)
        append(certificate.ogrnip)
    }
}