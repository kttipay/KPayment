package com.kttipay.payment

import kotlin.js.JsName

@JsName("ApplePaySession")
external object ApplePayAvailability {
    fun canMakePayments(): Boolean
}
