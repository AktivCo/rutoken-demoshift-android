package ru.rutoken.demoshift.bouncycastle.signature

import ru.rutoken.demoshift.bouncycastle.digest.GostR3411_1994Digest
import ru.rutoken.demoshift.bouncycastle.digest.GostR3411_2012_256Digest
import ru.rutoken.demoshift.bouncycastle.digest.GostR3411_2012_512Digest
import ru.rutoken.demoshift.bouncycastle.digest.Pkcs11Digest
import ru.rutoken.demoshift.pkcs11.GostOids
import ru.rutoken.pkcs11wrapper.`object`.key.Pkcs11PrivateKeyObject
import ru.rutoken.pkcs11wrapper.constant.standard.Pkcs11MechanismType
import ru.rutoken.pkcs11wrapper.main.Pkcs11Session
import ru.rutoken.pkcs11wrapper.mechanism.Pkcs11Mechanism
import ru.rutoken.pkcs11wrapper.rutoken.constant.RtPkcs11MechanismType

sealed class Signature(open val session: Pkcs11Session) {
    private lateinit var privateKey: Pkcs11PrivateKeyObject

    fun signInit(key: Pkcs11PrivateKeyObject) {
        privateKey = key
    }

    fun innerSign(mechanism: Pkcs11Mechanism, data: ByteArray): ByteArray =
        session.signManager.signAtOnce(data, mechanism, privateKey)

    abstract fun sign(data: ByteArray): ByteArray

    abstract fun makeDigest(): Pkcs11Digest
}

class GostR3410_2001Signature(session: Pkcs11Session) : Signature(session) {
    override fun sign(data: ByteArray) =
        innerSign(Pkcs11Mechanism.make(Pkcs11MechanismType.CKM_GOSTR3410), data)

    override fun makeDigest() = GostR3411_1994Digest(session)
}

class GostR3410_2012_256Signature(session: Pkcs11Session) : Signature(session) {
    override fun sign(data: ByteArray) =
        innerSign(Pkcs11Mechanism.make(Pkcs11MechanismType.CKM_GOSTR3410), data)

    override fun makeDigest() = GostR3411_2012_256Digest(session)
}

class GostR3410_2012_512Signature(session: Pkcs11Session) : Signature(session) {
    override fun sign(data: ByteArray) =
        innerSign(Pkcs11Mechanism.make(RtPkcs11MechanismType.CKM_GOSTR3410_512), data)

    override fun makeDigest() = GostR3411_2012_512Digest(session)
}

fun makeSignatureByHashOid(hashOid: ByteArray, session: Pkcs11Session) = when {
    hashOid.contentEquals(GostOids.DER_OID_3411_1994) -> GostR3410_2001Signature(session)

    hashOid.contentEquals(GostOids.DER_OID_3411_2012_256) -> GostR3410_2012_256Signature(session)

    hashOid.contentEquals(GostOids.DER_OID_3411_2012_512) -> GostR3410_2012_512Signature(session)

    else -> throw IllegalStateException(
        "Unsupported hash OID: " + hashOid.joinToString("") { "%02x".format(it) }
    )
}
