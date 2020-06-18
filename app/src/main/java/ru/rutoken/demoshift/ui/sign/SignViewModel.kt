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
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.tokenmanager.TokenManager
import ru.rutoken.demoshift.utils.BusinessRuleCase.FILE_UNAVAILABLE
import ru.rutoken.demoshift.utils.BusinessRuleException
import ru.rutoken.demoshift.utils.Status
import ru.rutoken.pkcs11wrapper.constant.standard.Pkcs11UserType
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.concurrent.ExecutionException


class SignViewModel(
    private val context: Context,
    private val tokenManager: TokenManager,
    tokenPin: String,
    userId: Int,
    documentUri: Uri
) : ViewModel() {
    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status

    private val _result = MutableLiveData<Result<String>>()
    val result: LiveData<Result<String>> = _result

    private fun signResult() =
        """
            -----BEGIN CMS-----
            MIAGCSqGSIb3DQEHAqCAMIACAQExDDAKBggqhQMHAQECAjCABgkqhkiG9w0BBwEAAKCAMIIDDzCCAr6gAwIBAgITEgBEULwnSEfjWQhIzwABAERQvDAIBgYqhQMCAgMwfzEjMCEGCSqGSIb3DQEJARYUc3VwcG9ydEBjcnlwdG9wcm8ucnUxCzAJBgNVBAYTAlJVMQ8wDQYDVQQHEwZNb3Njb3cxFzAVBgNVBAoTDkNSWVBUTy1QUk8gTExDMSEwHwYDVQQDExhDUllQVE8tUFJPIFRlc3QgQ2VudGVyIDIwHhcNMjAwNTA2MTAxMDExWhcNMjAwODA2MTAyMDExWjAOMQwwCgYDVQQDDAMxMjMwZjAfBggqhQMHAQEBATATBgcqhQMCAiMBBggqhQMHAQECAgNDAARA44gjMW2Ot4U0aRP2TlyjVSCwMFlJ0RfnK+ioa4eKqvf+TaLQcB0jdr1APOawIjYcK6AkYvC7A4Br27DAD81Lk6OCAX0wggF5MAsGA1UdDwQEAwIDyDAdBgNVHSUEFjAUBggrBgEFBQcDAgYIKwYBBQUHAwQwHQYDVR0OBBYEFMEtvg9Ds8s2XVKd8WFIJZeN6iZvMB8GA1UdIwQYMBaAFE6DPhRp7+xdepUrXxH+NzIWSVUrMFwGA1UdHwRVMFMwUaBPoE2GS2h0dHA6Ly90ZXN0Y2EuY3J5cHRvcHJvLnJ1L0NlcnRFbnJvbGwvQ1JZUFRPLVBSTyUyMFRlc3QlMjBDZW50ZXIlMjAyKDEpLmNybDCBrAYIKwYBBQUHAQEEgZ8wgZwwZAYIKwYBBQUHMAKGWGh0dHA6Ly90ZXN0Y2EuY3J5cHRvcHJvLnJ1L0NlcnRFbnJvbGwvdGVzdC1jYS0yMDE0X0NSWVBUTy1QUk8lMjBUZXN0JTIwQ2VudGVyJTIwMigxKS5jcnQwNAYIKwYBBQUHMAGGKGh0dHA6Ly90ZXN0Y2EuY3J5cHRvcHJvLnJ1L29jc3Avb2NzcC5zcmYwCAYGKoUDAgIDA0EAikRti5ZGBXxYidrPXB8+cp3thgip8OR8z+8LhDxn57jjq2E8xgwCadFe8Mgf9legjj4tgzfsLqo401KSYOUdgAAAMYIBizCCAYcCAQEwgZYwfzEjMCEGCSqGSIb3DQEJARYUc3VwcG9ydEBjcnlwdG9wcm8ucnUxCzAJBgNVBAYTAlJVMQ8wDQYDVQQHEwZNb3Njb3cxFzAVBgNVBAoTDkNSWVBUTy1QUk8gTExDMSEwHwYDVQQDExhDUllQVE8tUFJPIFRlc3QgQ2VudGVyIDICExIARFC8J0hH41kISM8AAQBEULwwCgYIKoUDBwEBAgKggZAwGAYJKoZIhvcNAQkDMQsGCSqGSIb3DQEHATAcBgkqhkiG9w0BCQUxDxcNMjAwNTE1MTI1MjQzWjAlBgkqhkiG9w0BCTQxGDAWMAoGCCqFAwcBAQICoQgGBiqFAwICEzAvBgkqhkiG9w0BCQQxIgQg9si0UwqlPtdycWp15GkkD+xDTqnOZ0EOyoJRCZ9xlEMwCAYGKoUDAgITBEAASErxQXFMj2GEhN71+i9iApLIHns/4ERHoIjd9v6ye1dHguejAUp0ADf9kPki3qvk7eC4YDwOlJ+jsF8t9M1bAAAAAAAA
            -----END CMS-----
        """.trimIndent()

    init {
        sign(tokenPin, documentUri)
    }

    private fun sign(tokenPin: String, documentUri: Uri) = viewModelScope.launch {
        try {
            val token = tokenManager.getSingleTokenAsync().await()
            _status.value = Status(context.getString(R.string.processing), true)

            withContext(Dispatchers.IO) {
                token.openSession(false).use { session ->
                    session.login(Pkcs11UserType.CKU_USER, tokenPin)
                    getDocumentInputStream(documentUri).use {  }
                }
            }

            // TODO: sign

            _status.value = Status(context.getString(R.string.done), false)
            _result.value = Result.success(signResult())
        } catch (e: Exception) {
            val exception = if (e is ExecutionException) (e.cause ?: e) else e

            _status.value = Status(null, false)
            _result.value = Result.failure(exception)
        }
    }

    private fun getDocumentInputStream(documentUri: Uri): InputStream {
        try {
            return context.contentResolver.openInputStream(documentUri)
                ?: throw BusinessRuleException(FILE_UNAVAILABLE)
        } catch (e: FileNotFoundException) {
            throw BusinessRuleException(FILE_UNAVAILABLE)
        }
    }
}