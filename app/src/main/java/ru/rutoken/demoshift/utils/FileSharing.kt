package ru.rutoken.demoshift.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import ru.rutoken.demoshift.R
import java.io.File

fun createFileSharingIntent(uri: Uri, context: Context): Intent {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.lastPathSegment!!))
        putExtra(Intent.EXTRA_STREAM, uri)
    }

    return Intent.createChooser(intent, context.getString(R.string.share_document))
}

fun copyAssetToCache(filename: String, context: Context) {
    File(context.cacheDir, "/$filename").outputStream()
        .use { output -> context.assets.open(filename).use { it.copyTo(output) } }
}