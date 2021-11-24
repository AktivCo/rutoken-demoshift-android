/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.getViewModel
import ru.rutoken.demoshift.databinding.ActivityMainBinding
import ru.rutoken.demoshift.pkcs11.Pkcs11Launcher
import ru.rutoken.demoshift.ui.installpanel.InstallPanelDialogFragment
import ru.rutoken.demoshift.ui.installpanel.isRutokenPanelInstalled

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = getViewModel()
        lifecycle.addObserver(get<Pkcs11Launcher>())
    }

    override fun onResume() {
        super.onResume()
        if (!isRutokenPanelInstalled(this)) {
            InstallPanelDialogFragment().show(supportFragmentManager, null)
        }
    }
}
