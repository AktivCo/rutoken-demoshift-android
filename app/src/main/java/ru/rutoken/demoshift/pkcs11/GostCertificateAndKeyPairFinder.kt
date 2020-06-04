package ru.rutoken.demoshift.pkcs11

import org.bouncycastle.cert.X509CertificateHolder
import ru.rutoken.pkcs11wrapper.`object`.certificate.Pkcs11X509PublicKeyCertificateObject
import ru.rutoken.pkcs11wrapper.`object`.key.Pkcs11GostPrivateKeyObject
import ru.rutoken.pkcs11wrapper.`object`.key.Pkcs11GostPublicKeyObject
import ru.rutoken.pkcs11wrapper.attribute.Pkcs11AttributeFactory
import ru.rutoken.pkcs11wrapper.constant.standard.Pkcs11AttributeType
import ru.rutoken.pkcs11wrapper.datatype.Pkcs11KeyPair
import ru.rutoken.pkcs11wrapper.main.Pkcs11Session
import java.util.*


object GostCertificateAndKeyPairFinder {
    /**
     * It is supposed that the user is logged in.
     */
    fun find(session: Pkcs11Session): List<GostCertificateAndKeyPair> {
        val certificates =
            session.objectManager.findObjectsAtOnce(Pkcs11X509PublicKeyCertificateObject::class.java)

        val result = mutableListOf<GostCertificateAndKeyPair>()

        for (certificate in certificates) {
            val x509CertificateHolder =
                X509CertificateHolder(certificate.getValueAttributeValue(session).byteArrayValue)

            result.add(
                GostCertificateAndKeyPair(
                    x509CertificateHolder,
                    findKeyPairByCertificate(session, x509CertificateHolder)
                )
            )
        }

        return result
    }

    private fun findKeyPairByCertificate(
        session: Pkcs11Session,
        certificateHolder: X509CertificateHolder
    ): Pkcs11KeyPair<Pkcs11GostPublicKeyObject, Pkcs11GostPrivateKeyObject> {
        val publicKey = session.objectManager.findSingleObject(
            Pkcs11GostPublicKeyObject::class.java,
            listOf(
                Pkcs11AttributeFactory.getInstance().makeAttribute(
                    Pkcs11AttributeType.CKA_VALUE,
                    getPublicKeyValue(certificateHolder)
                )
            )
        )

        val privateKey = session.objectManager.findSingleObject(
            Pkcs11GostPrivateKeyObject::class.java,
            listOf(publicKey.getIdAttributeValue(session))
        )

        return Pkcs11KeyPair(publicKey, privateKey)
    }

    private fun getPublicKeyValue(certificateHolder: X509CertificateHolder): ByteArray {
        val keyValue = certificateHolder.subjectPublicKeyInfo.parsePublicKey().encoded

        // Remove the key header (see ASN.1 Basic Encoding Rules)
        var pos = 2
        if (keyValue[1].toInt() and (1 shl 7) != 0)
            pos += keyValue[1].toInt() and (0xFF shr 1)

        return Arrays.copyOfRange(keyValue, pos, keyValue.size)
    }
}

data class GostCertificateAndKeyPair(
    val certificate: X509CertificateHolder,
    val keyPair: Pkcs11KeyPair<Pkcs11GostPublicKeyObject, Pkcs11GostPrivateKeyObject>
)
