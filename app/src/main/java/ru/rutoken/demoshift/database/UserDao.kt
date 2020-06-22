package ru.rutoken.demoshift.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id = :userId")
    suspend fun getUser(userId: Int): User

    @Query("SELECT * FROM user")
    suspend fun getUsers(): List<User>

    @Query("SELECT * FROM user")
    fun getUsersAsync(): LiveData<List<User>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}