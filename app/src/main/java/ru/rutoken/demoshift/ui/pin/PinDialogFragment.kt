package ru.rutoken.demoshift.ui.pin

import android.app.Dialog
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
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
        val builder = MaterialAlertDialogBuilder(context)
        binding = DialogFragmentPinBinding.inflate(layoutInflater)
        builder.setView(binding.root)

        builder.setPositiveButton(getString(R.string.pin_continue)) { _, _ ->
            val pin = binding.pinEditText.text.toString()
            setFragmentResult(DIALOG_RESULT_KEY, bundleOf(PIN_KEY to pin))
        }

        builder.setNegativeButton(getString(android.R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(BUTTON_POSITIVE).isEnabled =
                binding.pinEditText.text?.isNotEmpty() ?: false
        }

        binding.pinEditText.addTextChangedListener {
            dialog.getButton(BUTTON_POSITIVE).isEnabled =
                binding.pinEditText.text?.isNotEmpty() ?: false
        }

        return dialog
    }

    companion object {
        const val DIALOG_RESULT_KEY = "pin"
        const val PIN_KEY = "pin"
    }
}
