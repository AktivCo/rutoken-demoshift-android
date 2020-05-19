package ru.rutoken.demoshift.pkcs11

import com.sun.jna.Native
import ru.rutoken.pkcs11jna.RtPkcs11
import ru.rutoken.pkcs11wrapper.impl.Pkcs11Module
import ru.rutoken.pkcs11wrapper.lowlevel.jna.Pkcs11JnaLowLevelFactory
import ru.rutoken.pkcs11wrapper.rutoken.Pkcs11RutokenApi
import ru.rutoken.pkcs11wrapper.rutoken.constant.Pkcs11RutokenReturnValue
import ru.rutoken.pkcs11wrapper.rutoken.jna.Pkcs11RutokenJnaLowLevelApi

/**
 * Loads native pkcs11 library and initializes pkcs11wrapper.
 * Use this class as entry point to pkcs11wrapper.
 */
class RtPkcs11Module(name: String) : Pkcs11Module {
    private val pkcs11Api: Pkcs11RutokenApi = Pkcs11RutokenApi(
        Pkcs11RutokenJnaLowLevelApi(
            Native.load(name, RtPkcs11::class.java),
            Pkcs11JnaLowLevelFactory.Builder()
                .setReturnValueVendorFactory(Pkcs11RutokenReturnValue.Factory())
                .build()
        )
    )

    override fun getPkcs11Api(): Pkcs11RutokenApi = pkcs11Api
}