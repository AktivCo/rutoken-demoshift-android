/*
 * Copyright (c) 2020, Aktiv-Soft JSC.
 * See the LICENSE file at the top-level directory of this distribution.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.bouncycastle.digest

import org.bouncycastle.asn1.x509.AlgorithmIdentifier
import org.bouncycastle.operator.DigestCalculator
import org.bouncycastle.operator.DigestCalculatorProvider

class DigestProvider(val digestCalculator: DigestCalculator) : DigestCalculatorProvider {
    /**
     * We don't check algorithmIdentifier because Bouncy Castle supports only
     * CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001 algorithm
     */
    override fun get(algorithmIdentifier: AlgorithmIdentifier) = digestCalculator
}