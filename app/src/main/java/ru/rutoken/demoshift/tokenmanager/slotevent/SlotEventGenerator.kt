/*
 * Copyright (c) 2020, Aktiv-Soft JSC.
 * See the LICENSE file at the top-level directory of this distribution.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.tokenmanager.slotevent

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import ru.rutoken.pkcs11wrapper.main.IPkcs11Module
import ru.rutoken.pkcs11wrapper.main.Pkcs11Exception
import ru.rutoken.pkcs11wrapper.main.Pkcs11Slot


/**
 * Fills a [Channel] with slot events from a [IPkcs11Module.waitForSlotEvent] function.
 */
class SlotEventGenerator(
    private val slotEventChannel: SendChannel<SlotEvent>,
    private val pkcs11Module: IPkcs11Module
) {
    fun launchGeneration(scope: CoroutineScope) = scope.launch {
        while (isActive) {
            try {
                suspendForSlotEvent()?.let {
                    slotEventChannel.send(SlotEvent(it, it.slotInfo))
                }
            } catch (e: Pkcs11Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Turns blocking function into suspend function
     */
    private suspend fun suspendForSlotEvent(): Pkcs11Slot? =
        withContext(Dispatchers.IO) { pkcs11Module.waitForSlotEvent(false) }
}
