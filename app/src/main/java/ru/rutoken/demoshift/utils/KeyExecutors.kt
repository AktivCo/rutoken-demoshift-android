package ru.rutoken.demoshift.utils

import androidx.annotation.AnyThread
import androidx.core.util.Supplier
import java.util.concurrent.ExecutorService

@AnyThread
open class KeyExecutors<K>(private val executorServiceSupplier: Supplier<ExecutorService>) {
    private val executors = HashMap<K, ExecutorService>()

    fun get(key: K): ExecutorService {
        synchronized(executors) {
            return executors[key] ?: executorServiceSupplier.get().also { executors[key] = it }
        }
    }
}
