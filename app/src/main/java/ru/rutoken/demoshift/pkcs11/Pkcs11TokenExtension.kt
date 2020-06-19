/*
 * Copyright (c) 2020, Aktiv-Soft JSC.
 * See the LICENSE file at the top-level directory of this distribution.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.pkcs11

import ru.rutoken.pkcs11wrapper.main.Pkcs11Token

fun Pkcs11Token.getSerialNumber() = tokenInfo.serialNumber.trimEnd()