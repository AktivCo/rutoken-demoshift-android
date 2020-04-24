package ru.rutoken.demoshift.user

import java.util.*

data class User(
    val id: Int,
    val fullName: String,
    val position: String? = null,
    val organization: String? = null,
    val certificateExpires: Date? = null
)
