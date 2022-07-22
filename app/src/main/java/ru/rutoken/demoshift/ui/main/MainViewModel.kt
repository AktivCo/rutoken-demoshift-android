/*
 * Copyright (c) 2021, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.main

import androidx.lifecycle.ViewModel
import ru.rutoken.demoshift.pkcs11.Pkcs11Launcher

class MainViewModel(launcher: Pkcs11Launcher) : ViewModel() {
    init {
        launcher.launchPkcs11()
    }
}
