package com.kttipay.payment.api

enum class PaymentEnvironment {
    Production,
    Development;

    val isDebug: Boolean
        get() = this != Production
}

