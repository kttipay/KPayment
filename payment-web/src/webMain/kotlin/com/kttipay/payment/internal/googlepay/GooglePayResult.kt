package com.kttipay.payment.internal.googlepay

import com.kttipay.payment.api.PaymentErrorReason
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult

enum class GooglePayWebErrorCode(val message: String) {
    LOAD_PAYMENT_DATA_FAILED("Failed to load payment data"),
    TOKEN_EXTRACTION_FAILED("Failed to extract payment token from Google Pay response"),
    UNKNOWN("An unknown error occurred");
}

sealed interface GooglePayWebResult {
    data class Success(val token: String) : GooglePayWebResult

    data object Cancelled : GooglePayWebResult

    data class Error(
        val errorCode: GooglePayWebErrorCode,
        val additionalMessage: String? = null
    ) : GooglePayWebResult {
        val message: String
            get() = additionalMessage ?: errorCode.message
    }
}

internal fun GooglePayWebResult.toPaymentResult(): PaymentResult {
    return when (this) {
        is GooglePayWebResult.Success -> PaymentResult.Success(
            provider = PaymentProvider.GooglePay,
            token = token
        )

        GooglePayWebResult.Cancelled -> PaymentResult.Cancelled(
            provider = PaymentProvider.GooglePay
        )

        is GooglePayWebResult.Error -> PaymentResult.Error(
            provider = PaymentProvider.GooglePay,
            reason = errorCode.toPaymentErrorReason(),
            message = message
        )
    }
}

private fun GooglePayWebErrorCode.toPaymentErrorReason(): PaymentErrorReason {
    return when (this) {
        GooglePayWebErrorCode.LOAD_PAYMENT_DATA_FAILED -> PaymentErrorReason.InternalError
        GooglePayWebErrorCode.TOKEN_EXTRACTION_FAILED -> PaymentErrorReason.InternalError
        GooglePayWebErrorCode.UNKNOWN -> PaymentErrorReason.Unknown
    }
}
