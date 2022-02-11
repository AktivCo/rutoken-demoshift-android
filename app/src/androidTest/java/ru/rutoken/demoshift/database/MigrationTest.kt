/*
 * Copyright (c) 2020, Aktiv-Soft JSC.
 * See the LICENSE file at the top-level directory of this distribution.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase.CONFLICT_ABORT
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test

private const val DATABASE_NAME = "migration-test"
private const val VERSION_1 = 1
private const val VERSION_2 = 2

class MigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        Database::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate1To2() {
        val userV1 = ContentValues().apply {
            put("id", 5)
            put("fullName", "Ivan Ivanovich")
            put("position", "CEO")
            put("organization", "ACME LABS")
            put("certificateExpires", 1609459200L) // 01-01-2021
            put("certificateDerValue", "11223344".toByteArray())
            put("ckaId", "55667788".toByteArray())
            put("tokenSerialNumber", "tokenSerialNumber")
        }

        helper.createDatabase(DATABASE_NAME, VERSION_1).apply {
            insert("User", CONFLICT_ABORT, userV1)
            close()
        }

        val databaseV2 = helper.runMigrationsAndValidate(
            DATABASE_NAME,
            VERSION_2,
            true,
            MIGRATION_1_2
        )

        with(databaseV2.query("SELECT * FROM `users`")) {
            assertThat("Number of users differ", count, equalTo(1))

            moveToNext()

            assertThat(
                "ckaIds differ",
                getInt(getColumnIndex("id")),
                equalTo(userV1.getAsInteger("id"))
            )
            assertThat(
                "certificate DER values differ",
                getBlob(getColumnIndex("certificateDerValue")),
                equalTo(userV1.getAsByteArray("certificateDerValue"))
            )
            assertThat(
                "ckaIds differ",
                getBlob(getColumnIndex("ckaId")),
                equalTo(userV1.getAsByteArray("ckaId"))

            )
            assertThat(
                "Token serials differ",
                getString(getColumnIndex("tokenSerialNumber")),
                equalTo(userV1.getAsString("tokenSerialNumber"))
            )
        }
    }
}
