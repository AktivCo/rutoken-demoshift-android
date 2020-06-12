package ru.rutoken.demoshift.user

import java.util.*
import java.util.concurrent.atomic.AtomicInteger

data class User(
    val fullName: String,
    val position: String?,
    val organization: String?,
    val certificateExpires: Date?,
    val certificateDerValue: ByteArray,
    val ckaId: ByteArray,
    val tokenSerialNumber: String
) {
    val id = getIndex()

    companion object Index {
        private val index = AtomicInteger()

        fun getIndex() = index.getAndIncrement()
    }
}
