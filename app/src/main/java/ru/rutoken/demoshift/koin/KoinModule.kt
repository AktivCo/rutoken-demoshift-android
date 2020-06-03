package ru.rutoken.demoshift.koin

import org.koin.dsl.bind
import org.koin.dsl.module
import ru.rutoken.demoshift.pkcs11.RtPkcs11Module
import ru.rutoken.demoshift.tokenmanager.TokenManager
import ru.rutoken.pkcs11wrapper.main.Pkcs11Module

val koinModule = module {
    single { RtPkcs11Module() } bind Pkcs11Module::class
    single { TokenManager(get()) }
}