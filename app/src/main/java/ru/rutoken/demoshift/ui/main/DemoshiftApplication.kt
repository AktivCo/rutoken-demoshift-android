/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.main

import android.app.Application
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.rutoken.demoshift.koin.koinModule
import ru.rutoken.demoshift.tokenmanager.TokenManager

class DemoshiftApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DemoshiftApplication)
            modules(koinModule)
        }
        get<TokenManager>()
    }
}