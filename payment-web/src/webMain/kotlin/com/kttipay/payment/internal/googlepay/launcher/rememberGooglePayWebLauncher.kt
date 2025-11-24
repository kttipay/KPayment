package com.kttipay.payment.internal.googlepay.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kttipay.payment.WebPaymentManagerProvider
import com.kttipay.payment.internal.googlepay.GooglePayWebLauncherFactory
import com.kttipay.payment.internal.googlepay.GooglePayWebResult
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
@Composable
fun rememberGooglePayWebLauncher(
    onResult: (GooglePayWebResult) -> Unit
): IGooglePayWebLauncher {
    val paymentManager = WebPaymentManagerProvider.instance
    val config = remember(paymentManager) {
        paymentManager.googlePayConfig()
    }
    return remember(config, onResult) {
        GooglePayWebLauncherFactory().create(config, onResult)
    }
}