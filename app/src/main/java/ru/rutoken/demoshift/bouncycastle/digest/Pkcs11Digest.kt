package ru.rutoken.demoshift.bouncycastle.digest

import org.bouncycastle.crypto.Digest
import ru.rutoken.demoshift.pkcs11.GostOids
import ru.rutoken.pkcs11wrapper.constant.standard.Pkcs11MechanismType
import ru.rutoken.pkcs11wrapper.main.Pkcs11Session
import ru.rutoken.pkcs11wrapper.mechanism.Pkcs11Mechanism
import ru.rutoken.pkcs11wrapper.mechanism.parameter.Pkcs11ByteArrayMechanismParams
import ru.rutoken.pkcs11wrapper.rutoken.constant.RtPkcs11MechanismType


sealed class Pkcs11Digest(val session: Pkcs11Session) : Digest {
    private var isOperationInitialized = false
    abstract val mechanism: Pkcs11Mechanism

    override fun update(input: Byte) = update(ByteArray(1) { input })

    override fun update(input: ByteArray, inOff: Int, len: Int) =
        update(input.copyOfRange(inOff, inOff + len))

    private fun update(chunk: ByteArray) {
        if (!isOperationInitialized) {
            session.digestManager.digestInit(mechanism)
            isOperationInitialized = true
        }

        session.digestManager.digestUpdate(chunk)
    }

    override fun doFinal(out: ByteArray, outOff: Int): Int {
        val digest = session.digestManager.digestFinal().apply { copyInto(out, outOff) }
        isOperationInitialized = false
        return digest.size
    }

    override fun reset() {
        if (isOperationInitialized)
            doFinal(ByteArray(digestSize), 0)
    }
}

class GostR3411_1994Digest(session: Pkcs11Session) : Pkcs11Digest(session) {
    override val mechanism: Pkcs11Mechanism = Pkcs11Mechanism.make(
        Pkcs11MechanismType.CKM_GOSTR3411,
        Pkcs11ByteArrayMechanismParams(GostOids.DER_OID_3411_1994)
    )

    override fun getAlgorithmName() = "PKCS11-GOSTR3411-1994"

    override fun getDigestSize() = 32
}

class GostR3411_2012_256Digest(session: Pkcs11Session) : Pkcs11Digest(session) {
    override val mechanism: Pkcs11Mechanism = Pkcs11Mechanism.make(
        RtPkcs11MechanismType.CKM_GOSTR3411_12_256,
        Pkcs11ByteArrayMechanismParams(GostOids.DER_OID_3411_2012_256)
    )

    override fun getAlgorithmName() = "PKCS11-GOSTR3411-2012-256"

    override fun getDigestSize() = 32
}

class GostR3411_2012_512Digest(session: Pkcs11Session) : Pkcs11Digest(session) {
    override val mechanism: Pkcs11Mechanism = Pkcs11Mechanism.make(
        RtPkcs11MechanismType.CKM_GOSTR3411_12_512,
        Pkcs11ByteArrayMechanismParams(GostOids.DER_OID_3411_2012_512)
    )

    override fun getAlgorithmName() = "PKCS11-GOSTR3411-2012-512"

    override fun getDigestSize() = 64
}
