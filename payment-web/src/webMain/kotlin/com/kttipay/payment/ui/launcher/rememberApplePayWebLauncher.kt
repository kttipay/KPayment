package com.kttipay.payment.ui.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kttipay.payment.api.PaymentLauncher
import com.kttipay.payment.api.PaymentResult
import com.kttipay.payment.internal.applepay.launcher.ApplePayWebLauncher
import com.kttipay.payment.ui.LocalWebPaymentManager

/**
 * Returns a PaymentLauncher for Apple Pay on web platforms.
 *
 * @param onResult Callback invoked with payment result
 * @return PaymentLauncher for Apple Pay
 * @throws IllegalArgumentException if Apple Pay Web is not configured
 */
@Composable
fun rememberApplePayWebLauncher(onResult: (PaymentResult) -> Unit): PaymentLauncher {
    val manager = LocalWebPaymentManager.current
    val applePayWebConfig = manager.config.applePayWeb
    require(applePayWebConfig != null) {
        "Apple Pay Web configuration not found!"
    }

    return remember(applePayWebConfig, onResult) {
        ApplePayWebLauncher(
            config = applePayWebConfig,
            onResult = onResult,
        )
    }
}
