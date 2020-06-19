/*
 * Copyright (c) 2020, Aktiv-Soft JSC.
 * See the LICENSE file at the top-level directory of this distribution.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.*

@Database(entities = [User::class], version = 1)
@TypeConverters(Converters::class)
abstract class Database: RoomDatabase() {
    abstract fun userDao(): UserDao
}

object Converters {
    @TypeConverter
    @JvmStatic
    fun timestampToDate(value: Long) = Date(value)

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: Date) = date.time
}

const val AUTOGENERATE = 0