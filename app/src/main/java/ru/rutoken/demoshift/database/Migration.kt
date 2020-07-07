/*
 * Copyright (c) 2020, Aktiv-Soft JSC.
 * See the LICENSE file at the top-level directory of this distribution.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `users` ("
                    + "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "`certificateDerValue` BLOB NOT NULL, "
                    + "`ckaId` BLOB NOT NULL, "
                    + "`tokenSerialNumber` TEXT NOT NULL)"
        )
        database.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_users_certificateDerValue` "
                    + "ON `users` (`certificateDerValue`)"
        )

        database.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_users_tokenSerialNumber` "
                    + "ON `users` (`tokenSerialNumber`)"
        )

        database.execSQL(
            "INSERT INTO `users` (`id`, `certificateDerValue`, `ckaId`, `tokenSerialNumber`) "
                    + "SELECT `id`, `certificateDerValue`, `ckaId`, `tokenSerialNumber` from `User`"
        )

        database.execSQL("DROP TABLE `User`")
    }
}