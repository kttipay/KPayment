package com.kttipay.payment.internal.applepay

enum class ApplePayErrorCode(val code: String, val message: String) {
    PRESENT_FAILED("present_failed", "Failed to present Apple Pay sheet"),
    TOKEN_EXTRACTION_FAILED("token_extraction_failed", "Failed to extract payment token from Apple Pay response"),
    AUTHORIZATION_FAILED("authorization_failed", "Payment authorization failed"),
    UNKNOWN("unknown", "An unknown error occurred");

    companion object {
        fun fromCode(code: String): ApplePayErrorCode =
            entries.find { it.code == code } ?: UNKNOWN
    }
}
