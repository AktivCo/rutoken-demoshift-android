package ru.rutoken.demoshift.ui.adduser

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.pkcs11.GostCertificateAndKeyPairFinder
import ru.rutoken.demoshift.tokenmanager.TokenManager
import ru.rutoken.demoshift.user.User
import ru.rutoken.demoshift.user.UserRepository
import ru.rutoken.pkcs11wrapper.constant.standard.Pkcs11UserType
import ru.rutoken.pkcs11wrapper.main.Pkcs11Token
import java.util.*
import java.util.concurrent.ExecutionException

class AddUserViewModel(
    private val context: Context,
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository,
    tokenPin: String
) : ViewModel() {
    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status

    private val _result = MutableLiveData<Result<Unit>>()
    val result: LiveData<Result<Unit>> = _result

    init {
        addUser(tokenPin)
    }

    private suspend fun findCertificateAndKeyPair(token: Pkcs11Token, pin: String) =
        withContext(Dispatchers.IO) {
            token.openSession(false).use { session ->
                session.login(Pkcs11UserType.CKU_USER, pin).use {
                    val certKeys = GostCertificateAndKeyPairFinder.find(session)
                    check(certKeys.isNotEmpty()) { context.getString(R.string.no_certificate) }
                    check(certKeys.size == 1) {
                        context.getString(R.string.more_than_one_certificate)
                    }
                    return@withContext certKeys.first()
                }
            }
        }

    private fun addUser(tokenPin: String) = viewModelScope.launch {
        try {
            val token = tokenManager.getSingleTokenAsync().await()
            _status.value = Status(context.getString(R.string.processing), true)

            val certificateAndKeyPair = findCertificateAndKeyPair(token, tokenPin)

            val user = User(
                "Захаров Захар Захарович",
                "CEO",
                "ООО Рога и копыта",
                Date()
            )
            userRepository.addUser(user)

            _status.value = Status(context.getString(R.string.done), false)
            _result.value = Result.success(Unit)

        } catch (e: Exception) {
            val exception = if (e is ExecutionException) (e.cause ?: e) else e

            _status.value = Status(context.getString(R.string.error_text), false)
            _result.value = Result.failure(exception)
        }
    }

    data class Status(val message: String, val isProgress: Boolean)
}