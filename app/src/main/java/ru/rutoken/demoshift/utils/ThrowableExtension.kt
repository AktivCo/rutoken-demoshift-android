package ru.rutoken.demoshift.utils

import android.content.Context
import androidx.annotation.StringRes
import ru.rutoken.demoshift.R
import ru.rutoken.pkcs11wrapper.constant.IPkcs11ReturnValue
import ru.rutoken.pkcs11wrapper.constant.standard.Pkcs11ReturnValue
import ru.rutoken.pkcs11wrapper.main.Pkcs11Exception

fun Throwable.asReadableText(context: Context): String? {
    val message = when (this) {
        is Pkcs11Exception -> pkcs11ErrorMessages[this.code]
        is BusinessRuleException -> businessRuleStateMessages[this.case]
        else -> null
    }

    return if (message != null) context.getString(message) else null
}

private val pkcs11ErrorMessages = mapOf<IPkcs11ReturnValue, @StringRes Int>(
    Pkcs11ReturnValue.CKR_PIN_INCORRECT to R.string.pin_incorrect,
    Pkcs11ReturnValue.CKR_PIN_LOCKED to R.string.pin_locked,
    Pkcs11ReturnValue.CKR_SESSION_HANDLE_INVALID to R.string.connection_lost,
    Pkcs11ReturnValue.CKR_TOKEN_NOT_PRESENT to R.string.connection_lost
)

private val businessRuleStateMessages = mapOf(
    BusinessRuleCase.CERTIFICATE_NOT_FOUND to R.string.certificate_not_found,
    BusinessRuleCase.MORE_THAN_ONE_CERTIFICATE to R.string.more_than_one_certificate,
    BusinessRuleCase.KEY_PAIR_NOT_FOUND to R.string.key_pair_not_found,
    BusinessRuleCase.WRONG_RUTOKEN to R.string.wrong_rutoken,
    BusinessRuleCase.USER_DUPLICATES to R.string.user_duplicates,
    BusinessRuleCase.FILE_UNAVAILABLE to R.string.file_unavailable
)