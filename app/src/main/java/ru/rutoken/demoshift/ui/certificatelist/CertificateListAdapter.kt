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
import ru.rutoken.demoshift.databinding.FragmentUserBinding
import java.text.SimpleDateFormat
import java.util.*


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
        val binding = FragmentUserBinding.bind(view)

        binding.userFullName.text = certificate.fullName
        binding.userPosition.text =
            certificate.position ?: view.context.getString(R.string.field_not_set)
        binding.userOrganization.text = certificate.organization ?: view.context.getString(
            R.string.field_not_set
        )
        binding.userCertificateExpires.text = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
            .format(certificate.certificateExpires)

        binding.userCardView.setOnClickListener {
            listener.onClick(certificate)
        }
    }

    private fun getCertificate(position: Int) = certificates[position]

    override fun getItemCount() = certificates.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateViewHolder {
        val certificateView =
            FragmentUserBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
        return CertificateViewHolder(certificateView)
    }

    class CertificateViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}

interface CertificateCardListener {
    fun onClick(certificate: Certificate)
}
