package ru.rutoken.demoshift.user

import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

@AnyThread
class UserRepositoryImpl : UserRepository {
    private val users = Collections.synchronizedList(mutableListOf<User>())
    private val usersLiveData = MutableLiveData<List<User>>()

    init {
        addUser(
            User(
                "Иванов Иван Иванович",
                "Генеральный директор",
                "ООО Организация больших и малых закупок",
                Date()
            )
        )
        addUser(
            User(
                "Петров Иван Иванович",
                "Заместитель директора",
                "ООО Организация больших и малых закупок",
                Date()
            )
        )
        addUser(
            User(
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
