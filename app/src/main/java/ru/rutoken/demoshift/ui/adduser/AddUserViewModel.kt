package ru.rutoken.demoshift.ui.adduser

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.tokenmanager.TokenManager
import ru.rutoken.demoshift.user.User
import ru.rutoken.demoshift.user.UserRepository
import ru.rutoken.pkcs11wrapper.constant.standard.Pkcs11UserType
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future

class AddUserViewModel(
    private val context: Context,
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository
) :
    ViewModel() {
    private val _status = MutableLiveData<Status>()
    private val _result = MutableLiveData<Result<Unit>>()
    private var task: Future<*>? = null
    val status: LiveData<Status> = _status
    val result: LiveData<Result<Unit>> = _result

    init {
        // TODO: use pin from AddUserFragment
        addUser("12345678")
    }


    private fun addUser(tokenPin: String) {
        task?.cancel(true)
        task = Executors.newSingleThreadExecutor().submit {
            try {
                val token = tokenManager.getSingleToken().get()
                _status.postValue(Status(context.getString(R.string.processing), true))

                val session = token.openSession(false)
                session.login(Pkcs11UserType.CKU_USER, tokenPin)
                session.close()

                val user = User(
                    "Захаров Захар Захарович",
                    "CEO",
                    "ООО Рога и копыта",
                    Date()
                )
                userRepository.addUser(user)

                _status.postValue(Status(context.getString(R.string.done), false))
                _result.postValue(Result.success(Unit))

            } catch (e: Exception) {
                val exception = if (e is ExecutionException) (e.cause ?: e) else e

                _status.postValue(Status(context.getString(R.string.error_text), false))
                _result.postValue(Result.failure(exception))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        task?.cancel(true)
    }

    data class Status(val message: String, val isProgress: Boolean)
}