/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.main

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.databinding.ActivityMainBinding
import ru.rutoken.demoshift.pkcs11.Pkcs11Launcher

class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModel()
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycle.addObserver(get<Pkcs11Launcher>())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mainViewModel.bluetoothPermissionRequestDone.observe(this) { bluetoothPermissionRequestDone ->
                if (bluetoothPermissionRequestDone)
                    return@observe

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, BLUETOOTH_CONNECT))
                    showBluetoothPermissionRationale()
                else if (ContextCompat.checkSelfPermission(this, BLUETOOTH_CONNECT) == PERMISSION_DENIED)
                    launchBluetoothPermissionRequest()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun showBluetoothPermissionRationale() {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.ask_bluetooth_permission)
            .setPositiveButton(android.R.string.ok) { _, _ -> launchBluetoothPermissionRequest() }
            .setOnCancelListener { _ -> mainViewModel.finishBluetoothPermissionRequest() }
            .create()
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun launchBluetoothPermissionRequest() {
        requestPermissionLauncher.launch(BLUETOOTH_CONNECT)
        mainViewModel.finishBluetoothPermissionRequest()
    }
}
