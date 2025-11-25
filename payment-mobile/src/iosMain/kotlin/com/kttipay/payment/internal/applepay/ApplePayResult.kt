package com.kttipay.payment.internal.applepay

sealed interface ApplePayResult {
    /** Success with the UTF-8 JSON from PKPaymentToken.paymentData. */
    data class Success(
        val tokenJson: String,
        val transactionIdentifier: String?
    ) : ApplePayResult

    data object Cancelled : ApplePayResult

    data class Failure(
        val errorCode: ApplePayErrorCode,
        val additionalMessage: String? = null
    ) : ApplePayResult {
        val message: String
            get() = additionalMessage ?: errorCode.message
    }
}
