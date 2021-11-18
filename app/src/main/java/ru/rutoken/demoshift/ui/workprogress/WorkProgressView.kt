/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.workprogress

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import ru.rutoken.demoshift.databinding.WorkProgressBinding

class WorkProgressView : LinearLayout {
    private lateinit var binding: WorkProgressBinding

    constructor(context: Context?) : super(context, null) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs, 0) {
        init(context)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context?) {
        binding = WorkProgressBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setStatus(status: Status) {
        binding.statusTextView.text = status.message
        binding.progressBar.visibility = if (status.isProgress) View.VISIBLE else View.GONE
        binding.image.setImageDrawable(status.drawable)
        binding.image.isVisible = status.drawable != null
    }

    data class Status(
        val message: CharSequence?,
        val isProgress: Boolean = false,
        val drawable: Drawable? = null
    )
}