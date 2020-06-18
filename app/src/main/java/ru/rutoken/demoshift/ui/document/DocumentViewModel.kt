package ru.rutoken.demoshift.ui.document

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import ru.rutoken.demoshift.utils.copyAssetToCache

class DocumentViewModel(context: Context, document: String): ViewModel() {
    init {
        copyAssetToCache(document, context)
    }
}
