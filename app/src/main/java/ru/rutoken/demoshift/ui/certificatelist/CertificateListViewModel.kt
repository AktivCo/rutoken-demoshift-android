/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.certificatelist

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.database.UserEntity
import ru.rutoken.demoshift.pkcs11.GostObjectFinder
import ru.rutoken.demoshift.pkcs11.getSerialNumber
import ru.rutoken.demoshift.repository.User
import ru.rutoken.demoshift.repository.UserRepository
import ru.rutoken.demoshift.repository.makeUser
import ru.rutoken.demoshift.tokenmanager.TokenManager
import ru.rutoken.demoshift.utils.BusinessRuleCase.CERTIFICATE_NOT_FOUND
import ru.rutoken.demoshift.utils.BusinessRuleCase.USER_DUPLICATES
import ru.rutoken.demoshift.utils.BusinessRuleException
import ru.rutoken.demoshift.utils.Status
import ru.rutoken.pkcs11wrapper.constant.standard.Pkcs11UserType
import ru.rutoken.pkcs11wrapper.main.Pkcs11Token
import java.util.concurrent.ExecutionException


typealias Certificate = User

class CertificateListViewModel(
    private val context: Context,
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository,
    tokenPin: String
) : ViewModel() {
    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status

    private val _pkcs11Result = MutableLiveData<Result<List<Certificate>>>()
    val pkcs11Result: LiveData<Result<List<Certificate>>> = _pkcs11Result

    private val _addUserResult = MutableLiveData<Result<Unit>>()
    val addUserResult: LiveData<Result<Unit>> = _addUserResult

    init {
        createCertificateList(tokenPin)
    }

    private fun createCertificateList(tokenPin: String) = viewModelScope.launch {
        try {
            val token = tokenManager.getSingleTokenAsync().await()
            _status.value = Status(context.getString(R.string.processing), true)

            val serialNumber = withContext(Dispatchers.IO) {
                return@withContext token.getSerialNumber()
            }
            requireUniqueTokenSerial(serialNumber)

            val gostContainers = findContainers(token, tokenPin)
            _pkcs11Result.value = Result.success(gostContainers.map {
                makeUser(
                    UserEntity(
                        certificateDerValue = it.certificate.encoded,
                        ckaId = it.ckaId,
                        tokenSerialNumber = serialNumber
                    )
                )
            })

            _status.value = Status(context.getString(R.string.done), false)
        } catch (e: Exception) {
            val exception = if (e is ExecutionException) (e.cause ?: e) else e

            _status.value = Status(null, false)
            _pkcs11Result.value = Result.failure(exception)
        }
    }

    private suspend fun findContainers(token: Pkcs11Token, pin: String) =
        withContext(Dispatchers.IO) {
            token.openSession(false).use { session ->
                session.login(Pkcs11UserType.CKU_USER, pin).use {
                    val gostContainers = GostObjectFinder.findContainers(session)

                    if (gostContainers.isEmpty())
                        throw BusinessRuleException(CERTIFICATE_NOT_FOUND)

                    return@withContext gostContainers
                }
            }
        }

    private suspend fun requireUniqueTokenSerial(tokenSerial: String) {
        for (user in userRepository.getUsers()) {
            if (tokenSerial == user.userEntity.tokenSerialNumber)
                throw BusinessRuleException(USER_DUPLICATES)
        }
    }

    fun addUser(certificate: Certificate) = viewModelScope.launch {
        try {
            userRepository.addUser(certificate)
            _addUserResult.value = Result.success(Unit)

        } catch (e: SQLiteConstraintException) {
            _addUserResult.value = Result.failure(BusinessRuleException(USER_DUPLICATES))
        }
    }
}