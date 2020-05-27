package ru.rutoken.demoshift.tokenmanager.slotevent

import androidx.annotation.WorkerThread
import ru.rutoken.pkcs11wrapper.constant.Pkcs11Flag.CKF_TOKEN_PRESENT
import ru.rutoken.pkcs11wrapper.data.Pkcs11SlotInfo
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

class Pkcs11SlotEventProvider : AutoCloseable {
    private val listeners = CopyOnWriteArraySet<Listener>()
    private val slotEventQueue = LinkedBlockingQueue<Pkcs11SlotEvent>()
    private val previousSlotEvent = mutableMapOf<Long, Pkcs11SlotEvent>()
    private val executor = Executors.newSingleThreadExecutor()
    private val slotEventGenerator = Pkcs11SlotEventGenerator(slotEventQueue)

    init {
        executor.submit {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    handleSlotEvent(slotEventQueue.take())
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
        }
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    private fun handleSlotEvent(event: Pkcs11SlotEvent) {
        previousSlotEvent[event.slot.id]?.let {
            if (it.slotInfo.isTokenPresent == event.slotInfo.isTokenPresent)
                handleSlotEvent(makeFakeSlotEvent(it, event))
        }

        previousSlotEvent[event.slot.id] = event
        listeners.forEach { it.onPkcs11SlotEvent(event) }
    }

    override fun close() {
        slotEventGenerator.close()
        executor.shutdownNow()
    }

    private companion object {
        fun makeFakeSlotEvent(previousEvent: Pkcs11SlotEvent, newEvent: Pkcs11SlotEvent) =
            if (previousEvent.slotInfo.isTokenPresent)
                previousEvent.copyWithFlags(previousEvent.slotInfo.flags
                        and CKF_TOKEN_PRESENT.value.inv())
            else
                newEvent.copyWithFlags(newEvent.slotInfo.flags or CKF_TOKEN_PRESENT.value)

        private fun Pkcs11SlotEvent.copyWithFlags(flags: Long) = Pkcs11SlotEvent(
            slot, Pkcs11SlotInfo(
                slotInfo.slotDescription, slotInfo.manufacturerId,
                slotInfo.hardwareVersion, slotInfo.firmwareVersion, flags
            ), isFake = true
        )
    }

    interface Listener {
        @WorkerThread
        fun onPkcs11SlotEvent(event: Pkcs11SlotEvent)
    }
}
