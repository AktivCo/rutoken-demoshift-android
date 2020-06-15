package ru.rutoken.demoshift.utils

import android.content.Context
import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import ru.rutoken.demoshift.R
import java.io.File

fun createFileSharingIntent(filename: String, context: Context): Intent {
    val file = File(context.cacheDir, "/$filename")
    if (!file.exists()) {
        file.outputStream()
            .use { output -> context.assets.open(filename).use { it.copyTo(output) } }
    }

    val uri = FileProvider.getUriForFile(context, "ru.rutoken.demoshift.fileprovider", file)

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(filename))
        putExtra(Intent.EXTRA_STREAM, uri)
    }

    return Intent.createChooser(intent, context.getString(R.string.share_document))
}