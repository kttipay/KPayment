package com.kttipay.payment.api

interface PaymentLauncher {
    val provider: PaymentProvider

    fun launch(amount: String)
}
