package com.kttipay.payment.internal.applepay

import com.kttipay.payment.api.PaymentErrorReason
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult

/**
 * Error codes for Apple Pay Web failures.
 */
enum class ApplePayWebErrorCode(val code: String, val message: String) {
    /** Failed to begin the Apple Pay session (session.begin() failed) */
    SESSION_BEGIN_FAILED("session_begin_failed", "Failed to begin Apple Pay session"),

    /** Merchant validation failed (could be network error or invalid configuration) */
    MERCHANT_VALIDATION_FAILED("merchant_validation_failed", "Merchant validation failed"),

    /** Failed to extract payment token from Apple Pay response */
    TOKEN_EXTRACTION_FAILED("token_extraction_failed", "Failed to extract payment token from Apple Pay response"),

    /** Unknown error occurred */
    UNKNOWN("unknown", "An unknown error occurred");

    companion object {
        fun fromCode(code: String): ApplePayWebErrorCode =
            entries.find { it.code == code } ?: UNKNOWN
    }
}

sealed interface ApplePayWebResult {
    data class Success(val token: String) : ApplePayWebResult

    data object Cancelled : ApplePayWebResult

    data class Failure(
        val errorCode: ApplePayWebErrorCode,
        val additionalMessage: String? = null
    ) : ApplePayWebResult {
        val message: String
            get() = additionalMessage ?: errorCode.message
    }
}

/**
 * Converts ApplePayWebResult to PaymentResult for unified API.
 */
internal fun ApplePayWebResult.toPaymentResult(): PaymentResult {
    return when (this) {
        is ApplePayWebResult.Success -> PaymentResult.Success(
            provider = PaymentProvider.ApplePay,
            token = token
        )

        ApplePayWebResult.Cancelled -> PaymentResult.Cancelled(
            provider = PaymentProvider.ApplePay
        )

        is ApplePayWebResult.Failure -> PaymentResult.Error(
            provider = PaymentProvider.ApplePay,
            reason = errorCode.toPaymentErrorReason(),
            message = message
        )
    }
}

/**
 * Maps ApplePayWebErrorCode to PaymentErrorReason.
 */
private fun ApplePayWebErrorCode.toPaymentErrorReason(): PaymentErrorReason {
    return when (this) {
        ApplePayWebErrorCode.SESSION_BEGIN_FAILED -> PaymentErrorReason.NotAvailable
        ApplePayWebErrorCode.MERCHANT_VALIDATION_FAILED -> PaymentErrorReason.NetworkError
        ApplePayWebErrorCode.TOKEN_EXTRACTION_FAILED -> PaymentErrorReason.InternalError
        ApplePayWebErrorCode.UNKNOWN -> PaymentErrorReason.Unknown
    }
}
