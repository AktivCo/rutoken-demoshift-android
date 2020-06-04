package ru.rutoken.demoshift.koin

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.rutoken.demoshift.pkcs11.RtPkcs11Module
import ru.rutoken.demoshift.tokenmanager.TokenManager
import ru.rutoken.demoshift.ui.pin.PinViewModel
import ru.rutoken.pkcs11wrapper.impl.Pkcs11Module

val koinModule = module {
    single { RtPkcs11Module() } bind Pkcs11Module::class
    single { TokenManager(get()) }
    viewModel { PinViewModel(get()) }
}