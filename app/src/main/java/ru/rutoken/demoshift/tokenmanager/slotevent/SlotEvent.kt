package ru.rutoken.demoshift.tokenmanager.slotevent

import ru.rutoken.pkcs11wrapper.datatype.Pkcs11SlotInfo
import ru.rutoken.pkcs11wrapper.main.Pkcs11Slot

data class SlotEvent(
    val slot: Pkcs11Slot,
    val slotInfo: Pkcs11SlotInfo,
    val isFake: Boolean = false
)
