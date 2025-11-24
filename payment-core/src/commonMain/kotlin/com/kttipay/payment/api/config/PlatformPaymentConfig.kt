package com.kttipay.payment.api.config

import com.kttipay.payment.api.PaymentEnvironment

sealed interface PlatformPaymentConfig {
    val environment: PaymentEnvironment
    val googlePay: GooglePayConfig?
    val applePay: ApplePayConfig?
}

data class MobilePaymentConfig(
    override val environment: PaymentEnvironment,
    override val googlePay: GooglePayConfig? = null,
    val applePayMobile: ApplePayMobileConfig? = null
) : PlatformPaymentConfig {
    override val applePay: ApplePayConfig?
        get() = applePayMobile
}

data class WebPaymentConfig(
    override val environment: PaymentEnvironment,
    override val googlePay: GooglePayConfig? = null,
    val applePayWeb: ApplePayWebConfig? = null
) : PlatformPaymentConfig {
    override val applePay: ApplePayConfig?
        get() = applePayWeb
}
