/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.document

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.rutoken.demoshift.utils.copyAssetToCache
import java.io.File

class DocumentViewModel(context: Context) : ViewModel() {
    val documentUri: MutableLiveData<Uri> = MutableLiveData(
        FileProvider.getUriForFile(
            context,
            "ru.rutoken.demoshift.fileprovider",
            File(context.cacheDir, "/${DEFAULT_DOCUMENT}")
        )
    )

    init {
        copyAssetToCache(DEFAULT_DOCUMENT, context)
    }

    companion object {
        private const val DEFAULT_DOCUMENT = "sign_document.pdf"
    }
}
