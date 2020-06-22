package ru.rutoken.demoshift.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    indices = [
        Index(value = ["certificateDerValue"], unique = true),
        Index(value = ["tokenSerialNumber"], unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = AUTOGENERATE,
    val fullName: String,
    val position: String?,
    val organization: String?,
    val certificateExpires: Date,
    val certificateDerValue: ByteArray,
    val ckaId: ByteArray,
    val tokenSerialNumber: String
)
