package com.kttipay.payment.ui.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kttipay.payment.api.PaymentLauncher
import com.kttipay.payment.api.PaymentResult
import com.kttipay.payment.api.config.toGooglePayWebConfig
import com.kttipay.payment.internal.googlepay.GooglePayWebLauncherFactory
import com.kttipay.payment.ui.LocalWebPaymentManager

/**
 * Returns a PaymentLauncher for Google Pay on web platforms.
 *
 * @param onResult Callback invoked with payment result
 * @return PaymentLauncher for Google Pay
 * @throws IllegalArgumentException if Google Pay is not configured
 */
@Composable
fun rememberGooglePayWebLauncher(
    onResult: (PaymentResult) -> Unit
): PaymentLauncher {
    val manager = LocalWebPaymentManager.current
    val googleWebConfig = manager.config.googlePay
    require(googleWebConfig != null) {
        "Google Pay configuration not found!"
    }

    val googlePayConfig = googleWebConfig.toGooglePayWebConfig(manager.config.environment)

    return remember(googlePayConfig, onResult) {
        GooglePayWebLauncherFactory().create(
            config = googlePayConfig,
            onResult = onResult
        )
    }
}
