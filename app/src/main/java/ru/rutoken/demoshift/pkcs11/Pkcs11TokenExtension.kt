package ru.rutoken.demoshift.pkcs11

import ru.rutoken.pkcs11wrapper.main.Pkcs11Token

fun Pkcs11Token.getSerialNumber() = tokenInfo.serialNumber.trimEnd()