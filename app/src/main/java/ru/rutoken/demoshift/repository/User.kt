/*
 * Copyright (c) 2020, Aktiv-Soft JSC.
 * See the LICENSE file at the top-level directory of this distribution.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.repository

import org.bouncycastle.asn1.ASN1ObjectIdentifier
import org.bouncycastle.asn1.x500.style.BCStyle
import org.bouncycastle.cert.X509CertificateHolder
import ru.rutoken.demoshift.database.UserEntity
import java.text.SimpleDateFormat
import java.util.*

data class User(
    val userEntity: UserEntity,
    val fullName: String,
    val position: String?,
    val organization: String?,
    val certificateExpires: String,
    val inn: String?,
    val ogrn: String?,
    val algorithmId: String?
)

private const val INN_OID = "1.2.643.3.131.1.1"
private const val OGRN_OID = "1.2.643.100.1"

fun makeUser(
    userEntity: UserEntity
): User {
    val certificate = X509CertificateHolder(userEntity.certificateDerValue)

    check(certificate.subject.rdNs.all { !it.isMultiValued }) { "Multiple RDN values with the same type" }

    val cn = certificate.getIssuerRdnValue(BCStyle.CN)
    val surname = certificate.getIssuerRdnValue(BCStyle.SURNAME)
    val givenName = certificate.getIssuerRdnValue(BCStyle.GIVENNAME)

    val hasFullName = surname != null && givenName != null
    check(hasFullName || cn != null) { "Suitable RDNs are not found" }

    return User(
        userEntity = userEntity,
        fullName = if (hasFullName) "$surname $givenName" else cn!!,
        position = certificate.getIssuerRdnValue(BCStyle.T),
        organization = certificate.getIssuerRdnValue(BCStyle.O),
        certificateExpires = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
            .format(certificate.notAfter),
        inn = certificate.getIssuerRdnValue(ASN1ObjectIdentifier(INN_OID)),
        ogrn = certificate.getIssuerRdnValue(ASN1ObjectIdentifier(OGRN_OID)),
        algorithmId = certificate.subjectPublicKeyInfo.algorithm.algorithm.id
    )
}

private fun X509CertificateHolder.getIssuerRdnValue(type: ASN1ObjectIdentifier): String? {
    val rdn = subject.rdNs.find { it.first.type == type }
    return rdn?.first?.value?.toString()
}
