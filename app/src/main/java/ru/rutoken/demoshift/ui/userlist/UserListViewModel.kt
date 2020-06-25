package ru.rutoken.demoshift.ui.userlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.rutoken.demoshift.repository.UserRepository
import ru.rutoken.demoshift.database.User


class UserListViewModel(private val repository: UserRepository) : ViewModel() {
    fun getUsers(): LiveData<List<User>> = repository.getUsersAsync()

    fun removeUser(user: User) = viewModelScope.launch { repository.removeUser(user) }

    fun addUser(user: User) = viewModelScope.launch { repository.addUser(user) }
}