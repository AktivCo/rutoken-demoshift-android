/*
 * Copyright (c) 2021, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.rutoken.demoshift.pkcs11.Pkcs11Launcher

class MainViewModel(launcher: Pkcs11Launcher) : ViewModel() {
    private val _bluetoothPermissionRequestDone = MutableLiveData(false)

    val bluetoothPermissionRequestDone: LiveData<Boolean> get() = _bluetoothPermissionRequestDone

    init {
        launcher.launchPkcs11()
    }

    fun finishBluetoothPermissionRequest() {
        viewModelScope.launch {
            _bluetoothPermissionRequestDone.value = true
        }
    }
}
