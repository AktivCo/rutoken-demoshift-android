/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.main

import android.app.Application
import android.widget.Toast
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.koin.koinModule
import ru.rutoken.demoshift.tokenmanager.TokenManager
import ru.rutoken.rtpcscbridge.RtPcscBridge
import ru.rutoken.rttransport.RtTransport

class DemoshiftApplication : Application() {
    private val readerListObserver = object : RtTransport.PcscReaderObserver {
        override fun onReaderAdded(pcscReader: RtTransport.PcscReader) {
            Toast.makeText(applicationContext, getString(R.string.reader_added, pcscReader.name), Toast.LENGTH_SHORT)
                .show()
        }

        override fun onReaderRemoved(pcscReader: RtTransport.PcscReader) {
            Toast.makeText(applicationContext, getString(R.string.reader_removed, pcscReader.name), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DemoshiftApplication)
            modules(koinModule)
        }
        RtPcscBridge.enableDebugLogs()
        RtPcscBridge.setAppContext(this)
        RtPcscBridge.getTransport().addPcscReaderObserver(readerListObserver)
        RtPcscBridge.getTransportExtension().attachToLifecycle(this)
        get<TokenManager>()
    }
}