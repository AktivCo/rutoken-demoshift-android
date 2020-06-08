package ru.rutoken.demoshift.ui.main

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.rutoken.demoshift.koin.koinModule

class DemoshiftApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DemoshiftApplication)
            modules(koinModule)
        }
    }
}