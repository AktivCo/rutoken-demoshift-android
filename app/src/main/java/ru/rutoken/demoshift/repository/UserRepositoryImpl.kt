/*
 * Copyright (c) 2020, Aktiv-Soft JSC.
 * See the LICENSE file at the top-level directory of this distribution.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.repository

import androidx.annotation.AnyThread
import ru.rutoken.demoshift.database.Database
import ru.rutoken.demoshift.database.User


@AnyThread
class UserRepositoryImpl(database: Database) : UserRepository {
    private val userDao = database.userDao()

    override suspend fun getUser(userId: Int) = userDao.getUser(userId)

    override suspend fun getUsers() = userDao.getUsers()

    override fun getUsersAsync() = userDao.getUsersAsync()

    override suspend fun addUser(user: User) = userDao.addUser(user)

    override suspend fun removeUser(user: User) = userDao.deleteUser(user)
}
