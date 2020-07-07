/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.sign

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bouncycastle.cert.X509CertificateHolder
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.bouncycastle.signature.CmsSigner
import ru.rutoken.demoshift.bouncycastle.signature.Signature
import ru.rutoken.demoshift.bouncycastle.signature.makeSignatureByHashOid
import ru.rutoken.demoshift.pkcs11.GostObjectFinder
import ru.rutoken.demoshift.pkcs11.getSerialNumber
import ru.rutoken.demoshift.repository.User
import ru.rutoken.demoshift.repository.UserRepository
import ru.rutoken.demoshift.tokenmanager.TokenManager
import ru.rutoken.demoshift.ui.workprogress.WorkProgressView.Status
import ru.rutoken.demoshift.utils.BusinessRuleCase.*
import ru.rutoken.demoshift.utils.BusinessRuleException
import ru.rutoken.pkcs11wrapper.`object`.certificate.Pkcs11X509PublicKeyCertificateObject
import ru.rutoken.pkcs11wrapper.`object`.key.Pkcs11GostPrivateKeyObject
import ru.rutoken.pkcs11wrapper.attribute.Pkcs11ByteArrayAttribute
import ru.rutoken.pkcs11wrapper.constant.standard.Pkcs11AttributeType
import ru.rutoken.pkcs11wrapper.constant.standard.Pkcs11UserType
import ru.rutoken.pkcs11wrapper.main.Pkcs11Session
import ru.rutoken.pkcs11wrapper.main.Pkcs11Token
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.concurrent.ExecutionException


class SignViewModel(
    private val context: Context,
    private val tokenManager: TokenManager,
    private val tokenPin: String,
    private val documentUri: Uri,
    private val userRepository: UserRepository,
    private val userId: Int
) : ViewModel() {
    private val _status = MutableLiveData(
        Status(
            context.getText(R.string.waiting_token),
            false,
            context.getDrawable(R.drawable.ic_empty)
        )
    )
    val status: LiveData<Status> = _status

    private val _result = MutableLiveData<Result<String>>()
    val result: LiveData<Result<String>> = _result

    init {
        sign()
    }

    fun sign() = viewModelScope.launch {
        try {
            val user = userRepository.getUser(userId)
            val token = tokenManager.getSingleTokenAsync().await()
            _status.value = Status(context.getString(R.string.processing), true)

            val signResult = makeSign(user, token)

            _status.value = Status(context.getString(R.string.done), false)
            _result.value = Result.success(signResult)
        } catch (e: Exception) {
            val exception = if (e is ExecutionException) (e.cause ?: e) else e

            _status.value = Status(null, false)
            _result.value = Result.failure(exception)
        }
    }

    private fun openDocumentInputStream() = try {
        context.contentResolver.openInputStream(documentUri)
            ?: throw BusinessRuleException(FILE_UNAVAILABLE)
    } catch (e: FileNotFoundException) {
        throw BusinessRuleException(FILE_UNAVAILABLE, e)
    }

    private suspend fun makeSign(user: User, token: Pkcs11Token) = withContext(Dispatchers.IO) {
        if (user.userEntity.tokenSerialNumber != token.getSerialNumber())
            throw BusinessRuleException(WRONG_RUTOKEN)

        token.openSession(false).use { session ->
            session.login(Pkcs11UserType.CKU_USER, tokenPin).use {
                requireCertificate(session, user.userEntity.certificateDerValue)
                val certificate = X509CertificateHolder(user.userEntity.certificateDerValue)

                val keyPair = try {
                    GostObjectFinder.findKeyPairByCkaId(session, user.userEntity.ckaId)
                } catch (e: IllegalStateException) {
                    throw BusinessRuleException(KEY_PAIR_NOT_FOUND, e)
                }

                val signature = makeSignatureByHashOid(
                    keyPair.privateKey.getGostR3411ParamsAttributeValue(session).byteArrayValue,
                    session
                )
                openDocumentInputStream().use { documentStream ->
                    return@withContext signCms(
                        documentStream,
                        signature,
                        keyPair.privateKey,
                        certificate,
                        false
                    )
                }
            }
        }
    }

    private companion object {
        private fun requireCertificate(session: Pkcs11Session, value: ByteArray) {
            val isCertificatePresent = session.objectManager.findObjectsAtOnce(
                Pkcs11X509PublicKeyCertificateObject::class.java,
                listOf(Pkcs11ByteArrayAttribute(Pkcs11AttributeType.CKA_VALUE, value))
            ).size == 1

            if (!isCertificatePresent)
                throw BusinessRuleException(CERTIFICATE_NOT_FOUND)
        }

        private fun signCms(
            documentStream: InputStream,
            signature: Signature,
            privateKey: Pkcs11GostPrivateKeyObject,
            certificate: X509CertificateHolder,
            isAttached: Boolean
        ) = with(CmsSigner(signature)) {
            initSignature(privateKey, certificate, isAttached).use { stream ->
                documentStream.copyTo(stream)
            }
            finishSignaturePem()
        }
    }
}