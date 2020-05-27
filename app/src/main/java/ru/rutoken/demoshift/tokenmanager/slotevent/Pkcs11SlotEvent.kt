package ru.rutoken.demoshift.tokenmanager.slotevent

import ru.rutoken.pkcs11wrapper.data.Pkcs11SlotInfo
import ru.rutoken.pkcs11wrapper.main.Pkcs11Slot

data class Pkcs11SlotEvent(
    val slot: Pkcs11Slot,
    val slotInfo: Pkcs11SlotInfo,
    val isFake: Boolean = false
)
