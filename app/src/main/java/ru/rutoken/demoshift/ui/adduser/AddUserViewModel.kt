package ru.rutoken.demoshift.ui.adduser

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bouncycastle.asn1.ASN1ObjectIdentifier
import org.bouncycastle.asn1.x500.style.BCStyle
import org.bouncycastle.cert.X509CertificateHolder
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.pkcs11.GostContainer
import ru.rutoken.demoshift.pkcs11.GostObjectFinder
import ru.rutoken.demoshift.tokenmanager.TokenManager
import ru.rutoken.demoshift.user.User
import ru.rutoken.demoshift.user.UserRepository
import ru.rutoken.demoshift.utils.BusinessRuleCase.CERTIFICATE_NOT_FOUND
import ru.rutoken.demoshift.utils.BusinessRuleCase.MORE_THAN_ONE_CERTIFICATE
import ru.rutoken.demoshift.utils.BusinessRuleCase.USER_DUPLICATES
import ru.rutoken.demoshift.utils.BusinessRuleException
import ru.rutoken.demoshift.utils.Status
import ru.rutoken.demoshift.pkcs11.getSerialNumber
import ru.rutoken.pkcs11wrapper.constant.standard.Pkcs11UserType
import ru.rutoken.pkcs11wrapper.main.Pkcs11Token
import java.util.concurrent.ExecutionException


class AddUserViewModel(
    private val context: Context,
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository,
    tokenPin: String
) : ViewModel() {
    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status

    private val _result = MutableLiveData<Result<Unit>>()
    val result: LiveData<Result<Unit>> = _result

    init {
        addUser(tokenPin)
    }

    private fun addUser(tokenPin: String) = viewModelScope.launch {
        try {
            val token = tokenManager.getSingleTokenAsync().await()
            _status.value = Status(context.getString(R.string.processing), true)

            val gostContainer = findContainer(token, tokenPin)
            val serialNumber = withContext(Dispatchers.IO) {
                return@withContext token.getSerialNumber()
            }

            // FIXME: check whether using LiveData with DB will be ok
            if (userRepository.getUsers().value.orEmpty().any {
                    it.tokenSerialNumber == serialNumber
                })
                throw BusinessRuleException(USER_DUPLICATES)

            userRepository.addUser(makeUser(gostContainer, serialNumber))

            _status.value = Status(context.getString(R.string.done), false)
            _result.value = Result.success(Unit)

        } catch (e: Exception) {
            val exception = if (e is ExecutionException) (e.cause ?: e) else e

            _status.value = Status(null, false)
            _result.value = Result.failure(exception)
        }
    }

    private suspend fun findContainer(token: Pkcs11Token, pin: String) =
        withContext(Dispatchers.IO) {
            token.openSession(false).use { session ->
                session.login(Pkcs11UserType.CKU_USER, pin).use {
                    val gostContainers = GostObjectFinder.findContainers(session)

                    if (gostContainers.isEmpty())
                        throw BusinessRuleException(CERTIFICATE_NOT_FOUND)

                    if (gostContainers.size != 1)
                        throw BusinessRuleException(MORE_THAN_ONE_CERTIFICATE)

                    return@withContext gostContainers.first()
                }
            }
        }

    private fun makeUser(gostContainer: GostContainer, serial: String): User {
        val cert = gostContainer.certificate
        val expiresDate = cert.notAfter

        cert.subject.rdNs.forEach {
            check(!it.isMultiValued) { context.getString(R.string.multivalued_rdn) }
        }

        val cn = cert.getIssuerRdnValue(BCStyle.CN)
        val surname = cert.getIssuerRdnValue(BCStyle.SURNAME)
        val givenName = cert.getIssuerRdnValue(BCStyle.GIVENNAME)
        val position = cert.getIssuerRdnValue(BCStyle.T)
        val organization = cert.getIssuerRdnValue(BCStyle.O)

        val hasFullName = surname != null && givenName != null
        check(hasFullName || cn != null) { context.getString(R.string.rdn_not_found) }

        return User(
            if (hasFullName) "$surname $givenName" else cn!!,
            position,
            organization,
            expiresDate,
            cert.encoded,
            gostContainer.ckaId,
            serial
        )
    }

    private fun X509CertificateHolder.getIssuerRdnValue(type: ASN1ObjectIdentifier): String? {
        val rdn = subject.rdNs.find { it.first.type == type }
        return rdn?.first?.value?.toString()
    }
}