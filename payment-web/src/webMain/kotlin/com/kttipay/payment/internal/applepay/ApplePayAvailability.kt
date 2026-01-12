package com.kttipay.payment.internal.applepay

import kotlin.js.JsName

@JsName("ApplePaySession")
external object ApplePayAvailability {
    fun canMakePayments(): Boolean
}
