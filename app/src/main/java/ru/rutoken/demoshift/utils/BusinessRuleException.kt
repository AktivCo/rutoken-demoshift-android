/*
 * Copyright (c) 2020, Aktiv-Soft JSC.
 * See the LICENSE file at the top-level directory of this distribution.
 * All Rights Reserved.
 */

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
    WRONG_RUTOKEN,
    USER_DUPLICATES,
    FILE_UNAVAILABLE,
    TOO_MANY_TOKENS
}