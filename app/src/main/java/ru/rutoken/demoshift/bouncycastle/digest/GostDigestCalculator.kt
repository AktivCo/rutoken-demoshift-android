package ru.rutoken.demoshift.bouncycastle.digest

import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers
import org.bouncycastle.asn1.x509.AlgorithmIdentifier
import org.bouncycastle.crypto.io.DigestOutputStream
import org.bouncycastle.operator.DigestCalculator

class GostDigestCalculator(private val digest: Pkcs11Digest) : DigestCalculator {
    private val digestStream = DigestOutputStream(digest)

    override fun getAlgorithmIdentifier(): AlgorithmIdentifier =
        when (digest) {
            is GostR3411_1994Digest -> AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3411)
            is GostR3411_2012_256Digest ->
                AlgorithmIdentifier(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256)
            is GostR3411_2012_512Digest ->
                AlgorithmIdentifier(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512)
        }

    override fun getDigest() =
        ByteArray(digest.digestSize).also { digest.doFinal(it, 0) }

    override fun getOutputStream() = digestStream
}