package ru.rutoken.demoshift.user

import java.util.*
import java.util.concurrent.atomic.AtomicInteger

data class User(
    val fullName: String,
    val position: String? = null,
    val organization: String? = null,
    val certificateExpires: Date? = null
) {
    val id = getIndex()

    companion object Index {
        private val index = AtomicInteger()

        fun getIndex() = index.getAndIncrement()
    }
}
