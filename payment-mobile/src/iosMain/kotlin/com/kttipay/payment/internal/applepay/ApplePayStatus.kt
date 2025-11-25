package com.kttipay.payment.internal.applepay

data class ApplePayStatus(
    val canMakePayments: Boolean,
    val canSetupCards: Boolean
)
