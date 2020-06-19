/*
 * Copyright (c) 2020, Aktiv-Soft JSC.
 * See the LICENSE file at the top-level directory of this distribution.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.bouncycastle.signature

import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers
import org.bouncycastle.asn1.x509.AlgorithmIdentifier
import org.bouncycastle.crypto.io.DigestOutputStream
import org.bouncycastle.operator.ContentSigner
import ru.rutoken.demoshift.bouncycastle.digest.DigestProvider
import ru.rutoken.demoshift.bouncycastle.digest.GostDigestCalculator
import ru.rutoken.pkcs11wrapper.`object`.key.Pkcs11PrivateKeyObject

class GostContentSigner(private val signature: Signature) : ContentSigner {
    private val digestProvider: DigestProvider
    private val digestStream: DigestOutputStream

    init {
        val digest = signature.makeDigest()
        digestProvider = DigestProvider(GostDigestCalculator(digest))
        digestStream = DigestOutputStream(digest)
    }

    override fun getAlgorithmIdentifier() =
        when (signature) {
            is GostR3410_2001Signature ->
                AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_2001)
            is GostR3410_2012_256Signature ->
                AlgorithmIdentifier(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256)
            is GostR3410_2012_512Signature ->
                AlgorithmIdentifier(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256)
        }


    override fun getOutputStream() = digestStream

    override fun getSignature() = signature.sign(digestProvider.digestCalculator.digest)

    fun getDigestProvider() = digestProvider

    fun signInit(privateKey: Pkcs11PrivateKeyObject) = signature.signInit(privateKey)
}


