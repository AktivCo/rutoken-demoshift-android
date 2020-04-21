package ru.rutoken.demoshift

import java.util.*

data class User(
    val fullName: String,
    val position: String? = null,
    val organization: String? = null,
    val certificateExpires: Date? = null
)
