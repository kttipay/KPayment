package com.kttipay.payment.internal.applepay

sealed interface ApplePayWebResult {
    data class Success(val token: String) : ApplePayWebResult
    object Cancelled : ApplePayWebResult
    object Failure : ApplePayWebResult
}