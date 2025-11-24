package com.kttipay.payment.internal.googlepay

import com.kttipay.payment.api.config.GooglePayWebConfig
import com.kttipay.payment.internal.googlepay.launcher.GooglePayWebLauncher
import com.kttipay.payment.internal.googlepay.launcher.IGooglePayWebLauncher
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
internal class GooglePayWebLauncherFactory {
    fun create(
        config: GooglePayWebConfig,
        onResult: (GooglePayWebResult) -> Unit
    ): IGooglePayWebLauncher {
        val paymentClient = GooglePayPaymentClientImpl(config)
        val resultHandler = DefaultPaymentResultHandler(onResult)
        return GooglePayWebLauncher(
            paymentClient = paymentClient,
            resultHandler = resultHandler
        )
    }
}
