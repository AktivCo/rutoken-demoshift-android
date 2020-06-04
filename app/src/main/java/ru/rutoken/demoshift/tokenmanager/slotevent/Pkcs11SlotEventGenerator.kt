package ru.rutoken.demoshift.tokenmanager.slotevent

import ru.rutoken.pkcs11wrapper.main.Pkcs11Exception
import ru.rutoken.pkcs11wrapper.main.Pkcs11Module
import java.util.*
import java.util.concurrent.Executors


/**
 * Fills a queue with slot events from {@link IPkcs11Module#waitForSlotEvent(boolean)}
 */
class Pkcs11SlotEventGenerator(
    private val slotEventQueue: Queue<Pkcs11SlotEvent>,
    pkcs11Module: Pkcs11Module
) : AutoCloseable {
    private val executor = Executors.newSingleThreadExecutor()

    init {
        executor.submit {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    pkcs11Module.waitForSlotEvent(false)?.let {
                        slotEventQueue.add(Pkcs11SlotEvent(it, it.slotInfo))
                    }
                } catch (e: Pkcs11Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun close() {
        executor.shutdownNow()
    }
}
