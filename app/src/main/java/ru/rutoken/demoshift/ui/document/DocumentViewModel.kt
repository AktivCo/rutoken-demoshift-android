/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.document

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.graphics.pdf.PdfRenderer.Page
import android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.os.ParcelFileDescriptor.MODE_READ_ONLY
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.rutoken.demoshift.ui.pin.PinDialogFragment.Companion.PIN_KEY
import java.io.File
import java.io.InputStream
import java.util.UUID

class DocumentViewModel(private val appContext: Context) : ViewModel() {
    @Volatile
    private var currentDocumentUri: Uri

    private val _isDocumentPdf: MutableLiveData<Boolean> = MutableLiveData()
    val isDocumentPdf: LiveData<Boolean> by ::_isDocumentPdf

    private val _documentPdfBitmaps: MutableLiveData<List<Bitmap>> = MutableLiveData()
    val documentPdfBitmaps: LiveData<List<Bitmap>> by ::_documentPdfBitmaps

    private val _navigationArguments: MutableLiveData<SignFragmentNavigationArgs> = MutableLiveData()
    val navigationArguments: LiveData<SignFragmentNavigationArgs> by ::_navigationArguments

    init {
        currentDocumentUri = createDefaultDocumentAssetUri()
        _isDocumentPdf.value = true
    }

    fun updateDocument(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val isDocumentPdf = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(appContext.contentResolver.getType(uri)) == "pdf"

            if (isDocumentPdf) currentDocumentUri = uri
            _isDocumentPdf.postValue(isDocumentPdf)
        }
    }

    fun handleFragmentResult(bundle: Bundle) {
        viewModelScope.launch(Dispatchers.IO) {
            _navigationArguments.postValue(
                SignFragmentNavigationArgs(
                    checkNotNull(bundle.getString(PIN_KEY)),
                    currentDocumentUri
                )
            )
        }
    }

    fun renderPdfDocument(densityDpi: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _documentPdfBitmaps.postValue(
                ParcelFileDescriptor.open(createCacheFileFromUri(currentDocumentUri), MODE_READ_ONLY)
                    .use { descriptor ->
                        PdfRenderer(descriptor).use { renderer ->
                            buildList {
                                for (i in 0 until renderer.pageCount) {
                                    renderer.openPage(i).use { page ->
                                        add(renderSinglePdfPage(page, densityDpi))
                                    }
                                }
                            }
                        }
                    }
            )
        }
    }

    private fun renderSinglePdfPage(page: Page, densityDpi: Int): Bitmap {
        // https://developer.android.com/training/multiscreen/screendensities#dips-pels
        val width = page.width * (densityDpi / 160f)
        val height = page.height * (densityDpi / 160f)

        return createBitmap(width.toInt(), height.toInt()).also {
            Canvas(it).apply {
                drawColor(Color.WHITE)
                drawBitmap(it, 0f, 0f, null)
            }

            page.render(it, null, null, RENDER_MODE_FOR_DISPLAY)
        }
    }

    private fun createDefaultDocumentAssetUri(): Uri {
        val cachedAsset = File(appContext.cacheDir, "/$DEFAULT_DOCUMENT_ASSET_NAME").also {
            appContext.assets.open(DEFAULT_DOCUMENT_ASSET_NAME).use { inputStream ->
                it.copyInputStreamToFile(inputStream)
            }
        }

        return FileProvider.getUriForFile(appContext, "ru.rutoken.demoshift.fileprovider", cachedAsset)
    }

    private fun createCacheFileFromUri(uri: Uri): File? {
        return try {
            File(appContext.cacheDir, "${UUID.randomUUID()}.pdf").also {
                appContext.contentResolver.openInputStream(uri)?.use { inputStream ->
                    it.copyInputStreamToFile(inputStream)
                }
            }
        } catch (e: Exception) {
            Log.e(this::class.qualifiedName, "Failed to cache file.", e)
            null
        }
    }

    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        outputStream().use { output -> inputStream.copyTo(output) }
    }

    companion object {
        private const val DEFAULT_DOCUMENT_ASSET_NAME = "sign_document.pdf"
    }
}
