package ru.rutoken.demoshift.ui.adduser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.rutoken.demoshift.user.User
import ru.rutoken.demoshift.user.UserRepository

class AddUserViewModel : ViewModel() {
    private val _userAddingFailure = MutableLiveData<Exception>()
    private val _userAddingSucceed = MutableLiveData<Unit>()

    val userAddingSucceed: LiveData<Unit> = _userAddingSucceed
    val userAddingFailure: LiveData<Exception> = _userAddingFailure

    fun addUser(user: User) {
        try {
            UserRepository.getInstance().addUser(user)
            _userAddingSucceed.postValue(Unit)
        } catch (e: Exception) {
            _userAddingFailure.postValue(e)
        }
    }
}