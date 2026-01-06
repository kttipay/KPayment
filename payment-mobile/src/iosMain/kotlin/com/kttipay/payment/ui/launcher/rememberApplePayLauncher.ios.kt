package com.kttipay.payment.ui.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kttipay.payment.api.PaymentLauncher
import com.kttipay.payment.api.PaymentResult
import com.kttipay.payment.internal.applepay.ApplePayPaymentLauncher
import com.kttipay.payment.ui.LocalMobilePaymentManager

@Composable
actual fun rememberApplePayLauncher(
    onResult: (PaymentResult) -> Unit
): PaymentLauncher {
    val manager = LocalMobilePaymentManager.current
    val config = remember(manager) {
        manager.config.applePayMobile
            ?: error(
                "Apple Pay not configured. " +
                    "Provide applePayMobile in MobilePaymentConfig."
            )
    }

    return remember(config, onResult) {
        ApplePayPaymentLauncher(config = config, onResult = onResult)
    }
}
