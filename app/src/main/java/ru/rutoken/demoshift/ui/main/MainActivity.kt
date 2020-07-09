/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.get
import ru.rutoken.demoshift.databinding.ActivityMainBinding
import ru.rutoken.demoshift.tokenmanager.TokenManager
import ru.rutoken.demoshift.ui.installpanel.InstallPanelDialogFragment
import ru.rutoken.demoshift.ui.installpanel.isRutokenPanelInstalled

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycle.addObserver(get<TokenManager>())
    }

    override fun onResume() {
        super.onResume()
        if (!isRutokenPanelInstalled(this)) {
            InstallPanelDialogFragment().show(supportFragmentManager, null)
        }
    }
}
