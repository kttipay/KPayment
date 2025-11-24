package com.kttipay.payment.api

import org.kimplify.deci.Deci

interface PaymentLauncher {
    val provider: PaymentProvider
    fun launch(amount: Deci)
}