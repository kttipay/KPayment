package com.kttipay.payment.internal.applepay.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kttipay.payment.WebPaymentManagerProvider
import com.kttipay.payment.internal.applepay.ApplePayWebResult

@Composable
fun rememberApplePayWebLauncher(
    onResult: (ApplePayWebResult) -> Unit
): IApplePayWebLauncher {
    val paymentManager = WebPaymentManagerProvider.instance
    val config = remember(paymentManager) {
        paymentManager.applePayConfig()
    }
    return remember(config, onResult) {
        ApplePayWebLauncher(config, onResult)
    }
}
