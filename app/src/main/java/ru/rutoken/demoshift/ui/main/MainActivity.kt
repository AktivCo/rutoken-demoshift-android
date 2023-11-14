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

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = getViewModel()
        lifecycle.addObserver(get<Pkcs11Launcher>())
    }
}
