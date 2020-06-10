package ru.rutoken.demoshift.tokenmanager.slotevent

import androidx.annotation.AnyThread
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import ru.rutoken.pkcs11wrapper.constant.standard.Pkcs11Flag.CKF_TOKEN_PRESENT
import ru.rutoken.pkcs11wrapper.datatype.Pkcs11SlotInfo
import ru.rutoken.pkcs11wrapper.main.Pkcs11Module
import java.util.concurrent.CopyOnWriteArraySet

class SlotEventProvider(private val pkcs11Module: Pkcs11Module) {
    private val listeners = CopyOnWriteArraySet<Listener>()
    private val channel = Channel<SlotEvent>(Channel.UNLIMITED)
    private val previousSlotEvent = mutableMapOf<Long, SlotEvent>()

    fun launchEvents(scope: CoroutineScope) = scope.launch {
        SlotEventGenerator(channel, pkcs11Module).launchGeneration(this)

        while (isActive) {
            handleSlotEvent(channel.receive())
        }
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    private suspend fun handleSlotEvent(event: SlotEvent) {
        previousSlotEvent[event.slot.id]?.let {
            if (it.slotInfo.isTokenPresent == event.slotInfo.isTokenPresent)
                handleSlotEvent(makeFakeSlotEvent(it, event))
        }

        previousSlotEvent[event.slot.id] = event
        withContext(Dispatchers.Default) {
            listeners.forEach { it.onSlotEvent(event) }
        }
    }

    private companion object {
        fun makeFakeSlotEvent(previousEvent: SlotEvent, newEvent: SlotEvent) =
            if (previousEvent.slotInfo.isTokenPresent)
                previousEvent.copyWithFlags(
                    previousEvent.slotInfo.flags and CKF_TOKEN_PRESENT.asLong.inv()
                )
            else
                newEvent.copyWithFlags(newEvent.slotInfo.flags or CKF_TOKEN_PRESENT.asLong)

        private fun SlotEvent.copyWithFlags(flags: Long) = SlotEvent(
            slot, Pkcs11SlotInfo(
                slotInfo.slotDescription, slotInfo.manufacturerId,
                slotInfo.hardwareVersion, slotInfo.firmwareVersion, flags
            ), isFake = true
        )
    }

    @AnyThread
    interface Listener {
        fun onSlotEvent(event: SlotEvent)
    }
}
