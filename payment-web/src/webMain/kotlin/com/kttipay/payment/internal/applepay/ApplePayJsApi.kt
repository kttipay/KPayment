@file:OptIn(ExperimentalWasmJsInterop::class)

package com.kttipay.payment.internal.applepay

import kotlin.Int
import kotlin.JsFun
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsName

external object ApplePaySession {
    fun canMakePayments(): Boolean

    fun supportsVersion(version: Int): Boolean

    val STATUS_SUCCESS: Int
    val STATUS_FAILURE: Int
}

external class ApplePaySessionInstance {
    @JsName("onvalidatemerchant")
    var onValidateMerchant: ((JsAny) -> Unit)?

    @JsName("onpaymentauthorized")
    var onPaymentAuthorized: ((JsAny) -> Unit)?

    @JsName("oncancel")
    var onCancel: (() -> Unit)?

    fun begin()

    fun completeMerchantValidation(sessionData: JsAny)

    fun completePayment(status: Int)

    fun abort()
}

@JsFun("function(version, request) { return new ApplePaySession(version, request); }")
external fun createApplePaySession(version: Int, request: JsAny): ApplePaySessionInstance

@JsFun("function(evt) { return evt.validationURL; }")
external fun getValidationURL(evt: JsAny): String

@JsFun("function(evt) { return evt.payment.token; }")
external fun getPaymentToken(evt: JsAny): JsAny

@JsFun("function(str) { return JSON.parse(str); }")
external fun parseJsonToJs(str: String): JsAny

@JsFun("function(encoded) { return decodeURIComponent(encoded) }")
external fun decodeURIComponent(encoded: String): String

external object JSON {
    fun stringify(value: JsAny): String
}

external val window: WindowGlobal

external interface WindowGlobal {
    val location: LocationGlobal
}

external interface LocationGlobal {
    val hostname: String
}
