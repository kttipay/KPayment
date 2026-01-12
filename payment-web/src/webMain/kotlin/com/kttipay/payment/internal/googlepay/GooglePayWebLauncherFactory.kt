package com.kttipay.payment.internal.googlepay

import com.kttipay.payment.api.PaymentLauncher
import com.kttipay.payment.api.PaymentResult
import com.kttipay.payment.api.config.GooglePayWebConfig
import com.kttipay.payment.internal.googlepay.launcher.GooglePayWebLauncher
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
internal class GooglePayWebLauncherFactory {
    fun create(
        config: GooglePayWebConfig,
        onResult: (PaymentResult) -> Unit
    ): PaymentLauncher {
        val paymentClient = GooglePayPaymentClientImpl(config)
        return GooglePayWebLauncher(
            paymentClient = paymentClient,
            onResult = onResult
        )
    }
}
