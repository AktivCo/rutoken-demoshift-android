package ru.rutoken.demoshift.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CompletableDeferred

suspend fun <T> LiveData<T>.await(): T {
    val valueDeferred = CompletableDeferred<T>()
    val observer = Observer<T> { value -> valueDeferred.complete(value) }
    observeForever(observer)
    return valueDeferred.await().also { removeObserver(observer) }

}