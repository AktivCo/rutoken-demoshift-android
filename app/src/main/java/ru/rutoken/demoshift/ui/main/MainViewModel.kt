package ru.rutoken.demoshift.ui.main

import androidx.lifecycle.ViewModel
import ru.rutoken.demoshift.pkcs11.Pkcs11Launcher

class MainViewModel(launcher: Pkcs11Launcher) : ViewModel() {
    init {
        launcher.launchPkcs11()
    }
}
