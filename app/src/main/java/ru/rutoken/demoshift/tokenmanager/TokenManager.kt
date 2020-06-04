package ru.rutoken.demoshift.tokenmanager

import androidx.annotation.WorkerThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.SettableFuture
import ru.rutoken.demoshift.tokenmanager.slotevent.Pkcs11SlotEvent
import ru.rutoken.demoshift.tokenmanager.slotevent.Pkcs11SlotEventProvider
import ru.rutoken.pkcs11wrapper.datatype.Pkcs11InitializeArgs
import ru.rutoken.pkcs11wrapper.main.Pkcs11Module
import ru.rutoken.pkcs11wrapper.main.Pkcs11Token
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.Future


class TokenManager(private val pkcs11Module: Pkcs11Module) :
    Pkcs11SlotEventProvider.Listener,
    DefaultLifecycleObserver {

    private val tokens = Collections.synchronizedSet<Pkcs11Token>(mutableSetOf())
    private val listeners = CopyOnWriteArraySet<TokenListener>()
    private var waitTokenFuture: SettableFuture<Pkcs11Token>? = null

    private lateinit var slotEventProvider: Pkcs11SlotEventProvider

    override fun onStart(owner: LifecycleOwner) {
        pkcs11Module.initializeModule(Pkcs11InitializeArgs.Builder().setOsLockingOk(true).build())
        with(pkcs11Module.getSlotList(true)) {
            // If more than one token has been plugged in while the application was paused -
            // assume it as illegal state.
            if (size > 1)
                waitTokenFuture?.setException(MultipleTokensException("Too many tokens: $size"))
            forEach { addToken(it.token) }
        }

        slotEventProvider = Pkcs11SlotEventProvider(pkcs11Module).also {
            it.addListener(this)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        slotEventProvider.close()
        pkcs11Module.finalizeModule()
    }

    override fun onPkcs11SlotEvent(event: Pkcs11SlotEvent) {
        if (event.slotInfo.isTokenPresent)
            addToken(event.slot.token)
        else
            removeToken(event.slot.token)
    }

    fun getSingleToken(): Future<Pkcs11Token> {
        synchronized(tokens) {
            return when (tokens.size) {
                0 -> {
                    waitTokenFuture = waitTokenFuture ?: SettableFuture.create()
                    return waitTokenFuture!!
                }

                1 -> SettableFuture.create<Pkcs11Token>().apply { set(tokens.first()) }

                else -> SettableFuture.create<Pkcs11Token>().apply {
                    setException(MultipleTokensException("Too many tokens: ${tokens.size}"))
                }
            }
        }
    }

    private fun addToken(token: Pkcs11Token) {
        tokens.add(token)
        waitTokenFuture?.set(token)
        waitTokenFuture = null
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

    @WorkerThread
    interface TokenListener {
        fun onTokenAdded(token: Token)
        fun onTokenRemoved(token: Token)
    }
}

class MultipleTokensException(message: String) : Exception(message)
