/*
 * Copyright (c) 2020, Aktiv-Soft JSC.
 * See the LICENSE file at the top-level directory of this distribution.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.utils

import android.content.Context
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers
import ru.rutoken.demoshift.R

fun getAlgorithmNameById(context: Context, algorithmId: String?) =
    when (algorithmId) {
        CryptoProObjectIdentifiers.gostR3410_2001.toString() ->
            context.getString(R.string.gost_r_3410_2001)
        RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256.toString() ->
            context.getString(R.string.gost_r_3410_2012_256)
        RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512.toString() ->
            context.getString(R.string.gost_r_3410_2012_512)
        else -> null
    }
