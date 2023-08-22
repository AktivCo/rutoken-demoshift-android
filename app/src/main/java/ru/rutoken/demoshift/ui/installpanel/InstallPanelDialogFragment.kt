/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.installpanel

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.os.Build
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

@Suppress("DEPRECATION")
@SuppressLint("QueryPermissionsNeeded")
fun isRutokenPanelInstalled(activity: FragmentActivity): Boolean {
    val packageManager = activity.packageManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(PCSC_PACKAGE_NAME, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(PCSC_PACKAGE_NAME, 0)
            }
            true
        } catch (e: NameNotFoundException) {
            false
        }
    } else {
        val application = packageManager.getInstalledApplications(0).firstOrNull { it.packageName == PCSC_PACKAGE_NAME }
        application != null
    }
}
