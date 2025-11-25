package com.kttipay.payment.internal.applepay

interface ApplePayFactory {
    fun applePayStatus(): ApplePayStatus

    fun startPayment(
        request: ApplePayRequest,
        onResult: (ApplePayResult) -> Unit
    )

    fun presentSetupFlow(onFinished: (Boolean) -> Unit = {})
}
