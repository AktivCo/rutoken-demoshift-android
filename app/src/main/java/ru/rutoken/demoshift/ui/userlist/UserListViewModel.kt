package ru.rutoken.demoshift.ui.userlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.rutoken.demoshift.user.User
import ru.rutoken.demoshift.user.UserRepository

class UserListViewModel(
    private val repository: UserRepository = UserRepository.getInstance()
): ViewModel() {
    fun getUser(userId: Int): LiveData<User> = repository.getUser(userId)

    fun getUsers(): LiveData<List<User>> = repository.getUsers()

    fun removeUser(userId: Int) = repository.removeUser(userId)
}