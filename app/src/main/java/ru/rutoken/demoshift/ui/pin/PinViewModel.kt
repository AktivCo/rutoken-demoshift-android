package ru.rutoken.demoshift.ui.pin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.rutoken.demoshift.tokenmanager.TokenManager
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future

class PinViewModel : ViewModel() {
    private val _loginError = MutableLiveData<Exception>()
    private val _loginProceed = MutableLiveData<Unit>()
    private var task: Future<*>? = null

    val loginError: LiveData<Exception> = _loginError
    val loginProceed: LiveData<Unit> = _loginProceed

    fun login() {
        task?.cancel(true)
        task = Executors.newSingleThreadExecutor().submit {
            try {
                TokenManager.getSingleToken().get()
                _loginProceed.postValue(Unit)
            } catch (e: ExecutionException) {
                _loginError.postValue(e.cause as? Exception ?: e)
            } catch (e: InterruptedException) {
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        task?.cancel(true)
    }
}
