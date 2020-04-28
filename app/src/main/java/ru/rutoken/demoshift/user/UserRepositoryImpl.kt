package ru.rutoken.demoshift.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object UserRepositoryImpl : UserRepository {
    private val users = mutableListOf<User>()
    private val usersLiveData = MutableLiveData<List<User>>()

    override fun getUser(userId: Int): LiveData<User> {
        val data = MutableLiveData<User>()
        data.postValue(
            users.getOrNull(userId) ?: throw IllegalArgumentException("No user with id $userId")
        )
        return data
    }

    override fun getUsers(): LiveData<List<User>> {
        return usersLiveData
    }

    override fun addUser(user: User) {
        users.add(user)
        usersLiveData.postValue(users)
    }

    override fun removeUser(user: User) {
        if (!users.remove(user))
            throw IllegalArgumentException("No user with id ${user.id}")
        usersLiveData.postValue(users)
    }
}
