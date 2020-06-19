/*
 * Copyright (c) 2020, Aktiv-Soft JSC.
 * See the LICENSE file at the top-level directory of this distribution.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.pkcs11

import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers
import org.bouncycastle.cert.X509CertificateHolder
import ru.rutoken.pkcs11wrapper.`object`.certificate.Pkcs11X509PublicKeyCertificateObject
import ru.rutoken.pkcs11wrapper.`object`.key.Pkcs11GostPrivateKeyObject
import ru.rutoken.pkcs11wrapper.`object`.key.Pkcs11GostPublicKeyObject
import ru.rutoken.pkcs11wrapper.attribute.Pkcs11AttributeFactory
import ru.rutoken.pkcs11wrapper.attribute.Pkcs11ByteArrayAttribute
import ru.rutoken.pkcs11wrapper.attribute.longvalue.Pkcs11ObjectClassAttribute
import ru.rutoken.pkcs11wrapper.constant.standard.Pkcs11AttributeType
import ru.rutoken.pkcs11wrapper.constant.standard.Pkcs11ObjectClass
import ru.rutoken.pkcs11wrapper.datatype.Pkcs11KeyPair
import ru.rutoken.pkcs11wrapper.main.Pkcs11Session
import java.util.*


object GostObjectFinder {
    /**
     * It is supposed that the user is logged in.
     */
    fun findContainers(session: Pkcs11Session): List<GostContainer> {
        val certificates = session.objectManager
            .findObjectsAtOnce(Pkcs11X509PublicKeyCertificateObject::class.java)

        val result = mutableListOf<GostContainer>()

        for (certificate in certificates) {
            val x509CertificateHolder =
                X509CertificateHolder(certificate.getValueAttributeValue(session).byteArrayValue)

            if (x509CertificateHolder.subjectPublicKeyInfo.algorithm.algorithm !in setOf(
                    RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256,
                    RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512,
                    CryptoProObjectIdentifiers.gostR3410_2001
                )
            )
                continue

            val keyPair = try {
                findKeyPairByCertificate(session, x509CertificateHolder)
            } catch (ignore: IllegalStateException) {
                continue
            }

            result.add(
                GostContainer(
                    x509CertificateHolder,
                    keyPair,
                    keyPair.publicKey.getIdAttributeValue(session).byteArrayValue
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
            listOf(
                Pkcs11AttributeFactory.getInstance().makeAttribute(
                    Pkcs11AttributeType.CKA_VALUE,
                    getPublicKeyValue(certificateHolder)
                )
            )
        ) as Pkcs11GostPublicKeyObject

        val privateKey = session.objectManager.findSingleObject(
            listOf(
                publicKey.getIdAttributeValue(session),
                Pkcs11AttributeFactory.getInstance().makeAttribute(
                    Pkcs11AttributeType.CKA_CLASS,
                    Pkcs11ObjectClass.CKO_PRIVATE_KEY
                )
            )
        ) as Pkcs11GostPrivateKeyObject

        return Pkcs11KeyPair(publicKey, privateKey)
    }

    fun findKeyPairByCkaId(
        session: Pkcs11Session,
        ckaId: ByteArray
    ): Pkcs11KeyPair<Pkcs11GostPublicKeyObject, Pkcs11GostPrivateKeyObject> {
        val publicKey = session.objectManager.findSingleObject(
            listOf(
                Pkcs11ByteArrayAttribute(Pkcs11AttributeType.CKA_ID, ckaId),
                Pkcs11AttributeFactory.getInstance().makeAttribute(
                    Pkcs11AttributeType.CKA_CLASS,
                    Pkcs11ObjectClass.CKO_PUBLIC_KEY
                )
            )
        ) as Pkcs11GostPublicKeyObject

        val privateKey = session.objectManager.findSingleObject(
            listOf(
                Pkcs11ByteArrayAttribute(Pkcs11AttributeType.CKA_ID, ckaId),
                Pkcs11ObjectClassAttribute(
                    Pkcs11AttributeType.CKA_CLASS,
                    Pkcs11ObjectClass.CKO_PRIVATE_KEY
                )
            )
        ) as Pkcs11GostPrivateKeyObject

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

data class GostContainer(
    val certificate: X509CertificateHolder,
    val keyPair: Pkcs11KeyPair<Pkcs11GostPublicKeyObject, Pkcs11GostPrivateKeyObject>,
    val ckaId: ByteArray
)
