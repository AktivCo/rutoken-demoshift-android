/*
 * Copyright (c) 2021, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.text.Layout.Alignment.ALIGN_NORMAL
import android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE
import android.text.style.AlignmentSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.text.buildSpannedString
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import ru.rutoken.demoshift.R

fun Context.buildWaitingTokenString() = buildSpannedString {
    append(getText(R.string.connect_token), RelativeSizeSpan(1.3f), SPAN_INCLUSIVE_EXCLUSIVE)
    append(getText(R.string.waiting_token), AlignmentSpan.Standard(ALIGN_NORMAL), SPAN_INCLUSIVE_EXCLUSIVE)
}

/**
 * Opens URL in a browser using Custom Tabs API
 */
fun Context.launchCustomTabsUrl(url: Uri) {
    CustomTabsIntent.Builder().build().launchUrl(this, url)
}

fun Toolbar.applyTopInset() {
    doOnApplyWindowInsets { view, insets, padding ->
        val statusBarsInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
        view.updatePadding(top = padding.top + statusBarsInsets.top)
        insets
    }
}

fun View.applyBottomInset() {
    doOnApplyWindowInsets { view, insets, padding ->
        val navigationBarsInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        view.updatePadding(bottom = padding.bottom + navigationBarsInsets.bottom)
        insets
    }
}

fun View.doOnApplyWindowInsets(block: (View, WindowInsetsCompat, Rect) -> WindowInsetsCompat) {
    val initialPadding = recordInitialPaddingForView(this)

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        block(view, insets, initialPadding)
    }

    requestApplyInsetsWhenAttached()
}

private fun recordInitialPaddingForView(view: View) =
    Rect(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)

private fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        requestApplyInsets()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {
                view.removeOnAttachStateChangeListener(this)
                view.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(view: View) = Unit
        })
    }
}
