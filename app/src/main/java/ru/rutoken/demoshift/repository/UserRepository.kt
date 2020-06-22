package ru.rutoken.demoshift.repository

import androidx.lifecycle.LiveData
import ru.rutoken.demoshift.database.User

interface UserRepository {
    suspend fun getUser(userId: Int): User
    suspend fun getUsers(): List<User>
    fun getUsersAsync(): LiveData<List<User>>
    suspend fun addUser(user: User)
    suspend fun removeUser(user: User)
}
