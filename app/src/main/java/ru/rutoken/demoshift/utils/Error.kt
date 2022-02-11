/*
 * Copyright (c) 2020, Aktiv-Soft JSC.
 * See the LICENSE file at the top-level directory of this distribution.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.View
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import ru.rutoken.demoshift.R

/**
 * Shows error in the snackbar using given errorMessage with ability to copy it.
 * @param view The view used to make the snackbar. This should be contained within the
 * view hierarchy you want to display the snackbar.
 */
@MainThread
fun showError(view: View, errorMessage: String) {
    showSnackbar(view, errorMessage)
    Log.e(view.context.packageName, errorMessage)
}

/**
 * Shows error in the snackbar using given exception message.
 * @param view The view used to make the snackbar. This should be contained within the
 * view hierarchy you want to display the snackbar.
 */
@MainThread
fun showError(view: View, exception: Exception) {
    showError(view, exception.message ?: view.context.getString(R.string.error_text))
}

@MainThread
private fun showSnackbar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        .setAction(R.string.copy_text) {
            val manager =
                it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            manager.setPrimaryClip(ClipData.newPlainText("ERROR_TEXT", message))
        }
        .setBackgroundTint(ContextCompat.getColor(view.context, R.color.rutokenBlack))
        .setActionTextColor(ContextCompat.getColor(view.context, R.color.rutokenLightBlue))
        .show()
}
