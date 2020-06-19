/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.pin

import android.app.Dialog
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.databinding.DialogFragmentPinBinding

class PinDialogFragment : DialogFragment() {
    private lateinit var binding: DialogFragmentPinBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogFragmentPinBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setPositiveButton(getString(R.string.pin_continue)) { _, _ -> setResult() }
            .setNegativeButton(getString(android.R.string.cancel)) { dialog, _ -> dialog.cancel() }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(BUTTON_POSITIVE).isEnabled =
                binding.pinEditText.text?.isNotEmpty() ?: false
        }

        with(binding.pinEditText) {
            addTextChangedListener {
                dialog.getButton(BUTTON_POSITIVE).isEnabled =
                    text?.isNotEmpty() ?: false
            }
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (text?.isNotEmpty() == true) {
                        setResult()
                        dismiss()
                    }
                    true
                } else {
                    false
                }
            }
        }

        return dialog
    }

    private fun setResult() {
        val pin = binding.pinEditText.text.toString()
        setFragmentResult(DIALOG_RESULT_KEY, bundleOf(PIN_KEY to pin))
    }

    companion object {
        const val DIALOG_RESULT_KEY = "pin"
        const val PIN_KEY = "pin"
    }
}
