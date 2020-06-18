package ru.rutoken.demoshift.utils

class BusinessRuleException : Exception {
    val case: BusinessRuleCase

    constructor(case: BusinessRuleCase) : super(case.name) {
        this.case = case
    }

    constructor(case: BusinessRuleCase, e: Throwable) : super(case.name, e) {
        this.case = case
    }
}

enum class BusinessRuleCase {
    KEY_PAIR_NOT_FOUND,
    CERTIFICATE_NOT_FOUND,
    MORE_THAN_ONE_CERTIFICATE,
    WRONG_RUTOKEN,
    USER_DUPLICATES,
    FILE_UNAVAILABLE
}