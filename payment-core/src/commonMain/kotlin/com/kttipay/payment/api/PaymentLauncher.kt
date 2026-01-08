package com.kttipay.payment.api

import kotlinx.coroutines.flow.StateFlow

interface PaymentLauncher {
    val provider: PaymentProvider
    val isProcessing: StateFlow<Boolean>

    fun launch(amount: String)
}
