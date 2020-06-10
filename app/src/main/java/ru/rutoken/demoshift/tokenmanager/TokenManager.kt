package ru.rutoken.demoshift.tokenmanager

import androidx.annotation.AnyThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.rutoken.demoshift.tokenmanager.slotevent.SlotEvent
import ru.rutoken.demoshift.tokenmanager.slotevent.SlotEventProvider
import ru.rutoken.pkcs11wrapper.datatype.Pkcs11InitializeArgs
import ru.rutoken.pkcs11wrapper.main.Pkcs11Module
import ru.rutoken.pkcs11wrapper.main.Pkcs11Token
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet


class TokenManager(private val pkcs11Module: Pkcs11Module) :
    SlotEventProvider.Listener,
    DefaultLifecycleObserver {

    private val tokens = Collections.synchronizedSet<Pkcs11Token>(mutableSetOf())
    private val listeners = CopyOnWriteArraySet<TokenListener>()
    private var waitTokenDeferred: CompletableDeferred<Pkcs11Token>? = null

    private lateinit var eventJob: Job

    override fun onStart(owner: LifecycleOwner) {
        pkcs11Module.initializeModule(Pkcs11InitializeArgs.Builder().setOsLockingOk(true).build())
        with(pkcs11Module.getSlotList(true)) {
            // If more than one token has been plugged in while the application was paused -
            // assume it as illegal state.
            if (size > 1)
                waitTokenDeferred?.completeExceptionally(
                    MultipleTokensException("Too many tokens: $size")
                )
            forEach { addToken(it.token) }
        }

        eventJob = owner.lifecycleScope.launch {
            SlotEventProvider(pkcs11Module).also {
                it.addListener(this@TokenManager)
                it.launchEvents(this)
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        eventJob.cancel()
        pkcs11Module.finalizeModule()
    }

    override fun onSlotEvent(event: SlotEvent) {
        if (event.slotInfo.isTokenPresent)
            addToken(event.slot.token)
        else
            removeToken(event.slot.token)
    }

    fun getSingleTokenAsync(): Deferred<Pkcs11Token> {
        synchronized(tokens) {
            return when (tokens.size) {
                0 -> {
                    waitTokenDeferred = waitTokenDeferred ?: CompletableDeferred()
                    return waitTokenDeferred!!
                }

                1 -> CompletableDeferred(tokens.first())

                else -> CompletableDeferred<Pkcs11Token>().apply {
                    completeExceptionally(MultipleTokensException("Too many tokens: ${tokens.size}"))
                }
            }
        }
    }

    private fun addToken(token: Pkcs11Token) {
        tokens.add(token)
        waitTokenDeferred?.complete(token)
        waitTokenDeferred = null
    }

    private fun removeToken(token: Pkcs11Token) {
        tokens.remove(token)
    }

    fun addTokenListener(listener: TokenListener) {
        listeners.add(listener)
    }

    fun removeTokenListener(listener: TokenListener) {
        listeners.remove(listener)
    }

    @AnyThread
    interface TokenListener {
        fun onTokenAdded(token: Token)
        fun onTokenRemoved(token: Token)
    }
}

class MultipleTokensException(message: String) : Exception(message)
