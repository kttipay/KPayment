package com.kttipay.payment.internal.googlepay

import com.kttipay.payment.internal.logging.KPaymentLogger
import com.kttipay.payment.api.PaymentErrorReason

internal interface GooglePayWebResultHandler {
    fun onSuccess(token: GooglePayToken)
    fun onCancelled()
    fun onError(error: Throwable)
    fun onNotAvailable()
}

internal class DefaultPaymentResultHandler(
    private val callback: (GooglePayWebResult) -> Unit
) : GooglePayWebResultHandler {

    override fun onSuccess(token: GooglePayToken) {
        callback(GooglePayWebResult.Success(token = token.value))
    }

    override fun onCancelled() {
        KPaymentLogger.tag(TAG).d("Payment cancelled by user")
        callback(GooglePayWebResult.Cancelled)
    }

    override fun onError(error: Throwable) {
        val errorMessage = buildString {
            append("Payment failed: ")
            append(error::class.simpleName ?: "Unknown")
            append(" - ")
            append(error.message ?: error.toString())
            error.cause?.message?.let { append(" | Cause: ").append(it) }
        }
        KPaymentLogger.tag(TAG).e(errorMessage, error)
        callback(
            GooglePayWebResult.Error(
                reason = PaymentErrorReason.Unknown,
                message = errorMessage
            )
        )
    }

    override fun onNotAvailable() {
        KPaymentLogger.tag(TAG).d("Google Pay not available")
        callback(
            GooglePayWebResult.Error(
                reason = PaymentErrorReason.NotAvailable,
                message = "Google Pay not available"
            )
        )
    }

    private companion object {
        const val TAG = "GoogleWeb"
    }
}