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
    val paymentManager = LocalMobilePaymentManager.current
    val config = remember(paymentManager) {
        paymentManager.applePayConfig()
            ?: error(
                "Apple Pay not configured. " +
                "Call MobilePaymentManager.initialize() with applePayMobile config."
            )
    }

    return remember(config, onResult) {
        ApplePayPaymentLauncher(config = config, onResult = onResult)
    }
}
