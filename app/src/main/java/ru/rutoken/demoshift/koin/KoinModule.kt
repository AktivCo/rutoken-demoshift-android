/*
 * Copyright (c) 2020, Aktiv-Soft JSC.
 * See the LICENSE file at the top-level directory of this distribution.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.koin

import android.net.Uri
import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.rutoken.demoshift.database.Database
import ru.rutoken.demoshift.pkcs11.RtPkcs11Module
import ru.rutoken.demoshift.tokenmanager.TokenManager
import ru.rutoken.demoshift.ui.certificatelist.CertificateListViewModel
import ru.rutoken.demoshift.ui.document.DocumentViewModel
import ru.rutoken.demoshift.repository.UserRepository
import ru.rutoken.demoshift.repository.UserRepositoryImpl
import ru.rutoken.demoshift.ui.sign.SignViewModel
import ru.rutoken.demoshift.ui.userlist.UserListViewModel
import ru.rutoken.pkcs11wrapper.main.Pkcs11Module

val koinModule = module {
    single { RtPkcs11Module() } bind Pkcs11Module::class
    single { TokenManager(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single {
        Room.databaseBuilder(androidContext(), Database::class.java, "demoshift_database").build()
    }
    viewModel { (tokenPin: String) ->
        CertificateListViewModel(androidContext(), get(), get(), tokenPin)
    }
    viewModel { UserListViewModel(get()) }
    viewModel { (tokenPin: String, documentUri: Uri, userId: Int) ->
        SignViewModel(androidContext(), get(), tokenPin, documentUri, get(), userId)
    }
    viewModel { DocumentViewModel(androidContext()) }
}