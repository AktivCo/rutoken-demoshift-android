/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.installpanel

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.rutoken.demoshift.R

const val PCSC_PACKAGE_NAME = "ru.rutoken"

class InstallPanelDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.title_no_pcsc)
            .setMessage(R.string.message_no_pcsc)
            .setNeutralButton(R.string.exit) { _, _ -> requireActivity().finish() }
            .setPositiveButton(R.string.install) { _, _ -> installPanel() }
            .create().also { it.setCanceledOnTouchOutside(false) }
    }

    private fun installPanel() = with(requireActivity()) {
        try {
            startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$PCSC_PACKAGE_NAME"))
            )
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$PCSC_PACKAGE_NAME")
                )
            )
        }
        finish()
    }
}

fun isRutokenPanelInstalled(activity: FragmentActivity): Boolean {
    val application = activity.packageManager.getInstalledApplications(0).firstOrNull {
        it.packageName == PCSC_PACKAGE_NAME
    }

    return application != null
}