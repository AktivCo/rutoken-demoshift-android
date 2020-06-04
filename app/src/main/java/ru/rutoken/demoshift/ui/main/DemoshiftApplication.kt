package ru.rutoken.demoshift.ui.main

import android.app.Application
import org.koin.core.context.startKoin
import ru.rutoken.demoshift.koin.koinModule

class DemoshiftApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin { modules(koinModule) }
    }
}