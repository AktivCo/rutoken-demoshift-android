package ru.rutoken.demoshift.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

object UserRepositoryImpl : UserRepository {
    private val users = mutableListOf<User>()
    private val usersLiveData = MutableLiveData<List<User>>()

    init {
        addUser(
            User(
                0,
                "Иванов Иван Иванович",
                "Генеральный директор",
                "ООО Организация больших и малых закупок",
                Date()
            )
        )
        addUser(
            User(
                5,
                "Петров Иван Иванович",
                "Заместитель директора",
                "ООО Организация больших и малых закупок",
                Date()
            )
        )
        addUser(
            User(
                6,
                "Сидоров Иван Иванович",
                "Секретарь",
                "ООО Организация больших и малых закупок",
                Date()
            )
        )

    }

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

    override fun removeUser(userId: Int) {
        val user =
            users.getOrNull(userId) ?: throw IllegalArgumentException("No user with id $userId")

        users.remove(user)
        usersLiveData.postValue(users)
    }
}
