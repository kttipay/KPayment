package com.kttipay.payment.api

sealed interface PaymentResult {
    val provider: PaymentProvider

    data class Success(
        override val provider: PaymentProvider,
        val token: String
    ) : PaymentResult

    data class Cancelled(
        override val provider: PaymentProvider
    ) : PaymentResult

    data class Error(
        override val provider: PaymentProvider,
        val reason: PaymentErrorReason = PaymentErrorReason.Unknown,
        val message: String? = null
    ) : PaymentResult
}

enum class PaymentErrorReason {
    Timeout,
    ApiNotConnected,
    ConnectionSuspendedDuringCall,
    DeveloperError,
    InternalError,
    Interrupted,
    NetworkError,
    SignInRequired,
    NotAvailable,
    Unknown
}

