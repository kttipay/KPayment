package com.kttipay.payment.internal.googlepay

import com.kttipay.payment.api.PaymentErrorReason

sealed interface GooglePayWebResult {
    data class Success(val token: String) : GooglePayWebResult

    data object Cancelled : GooglePayWebResult

    data class Error(
        val reason: PaymentErrorReason = PaymentErrorReason.Unknown,
        val message: String? = null
    ) : GooglePayWebResult
}
