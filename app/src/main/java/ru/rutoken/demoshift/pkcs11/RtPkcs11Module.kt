/*
 * Copyright (c) 2020, Aktiv-Soft JSC.
 * See the LICENSE file at the top-level directory of this distribution.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.pkcs11

import com.sun.jna.Native
import ru.rutoken.pkcs11jna.RtPkcs11
import ru.rutoken.pkcs11wrapper.lowlevel.jna.Pkcs11JnaLowLevelFactory
import ru.rutoken.pkcs11wrapper.main.Pkcs11BaseModule
import ru.rutoken.pkcs11wrapper.rutoken.constant.RtPkcs11MechanismType
import ru.rutoken.pkcs11wrapper.rutoken.constant.RtPkcs11ReturnValue
import ru.rutoken.pkcs11wrapper.rutoken.jna.RtPkcs11JnaLowLevelApi
import ru.rutoken.pkcs11wrapper.rutoken.main.RtPkcs11Api
import ru.rutoken.pkcs11wrapper.rutoken.main.RtPkcs11HighLevelFactory

/**
 * Loads native pkcs11 library and initializes pkcs11wrapper.
 * Use this class as entry point to pkcs11wrapper.
 */
class RtPkcs11Module(name: String = "rtpkcs11ecp") : Pkcs11BaseModule(
    RtPkcs11Api(
        RtPkcs11JnaLowLevelApi(
            Native.load(name, RtPkcs11::class.java),
            Pkcs11JnaLowLevelFactory.Builder()
                .setReturnValueVendorFactory(RtPkcs11ReturnValue.Factory())
                .setMechanismTypeVendorFactory(RtPkcs11MechanismType.Factory())
                .build()
        )
    ),
    RtPkcs11HighLevelFactory()
)
