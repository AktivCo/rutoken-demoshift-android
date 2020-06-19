/*
 * Copyright (c) 2020, Aktiv-Soft JSC.
 * See the LICENSE file at the top-level directory of this distribution.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.bouncycastle.signature

import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.cms.ContentInfo
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cms.CMSSignedDataStreamGenerator
import org.bouncycastle.cms.SignerInfoGeneratorBuilder
import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import ru.rutoken.pkcs11wrapper.`object`.key.Pkcs11PrivateKeyObject
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.StringWriter

class CmsSigner(signature: Signature) {
    private val signer = GostContentSigner(signature)
    private val outStream = ByteArrayOutputStream()

    fun initSignature(
        privateKey: Pkcs11PrivateKeyObject,
        certificate: X509CertificateHolder,
        isAttached: Boolean
    ): OutputStream {
        signer.signInit(privateKey)

        val generator = CMSSignedDataStreamGenerator().apply {
            addCertificate(certificate)
            addSignerInfoGenerator(
                SignerInfoGeneratorBuilder(signer.getDigestProvider()).build(signer, certificate)
            )
        }

        return generator.open(outStream, isAttached)
    }

    fun finishSignatureDer(): ByteArray = outStream.toByteArray()

    fun finishSignaturePem(): String {
        val writer = StringWriter()
        JcaPEMWriter(writer).use {
            it.writeObject(
                ContentInfo.getInstance(ASN1Sequence.fromByteArray(outStream.toByteArray()))
            )
        }
        return writer.toString()
    }
}