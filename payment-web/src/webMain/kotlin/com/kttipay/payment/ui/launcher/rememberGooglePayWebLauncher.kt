package com.kttipay.payment.ui.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kttipay.payment.api.config.toGooglePayWebConfig
import com.kttipay.payment.internal.googlepay.GooglePayWebLauncherFactory
import com.kttipay.payment.internal.googlepay.GooglePayWebResult
import com.kttipay.payment.internal.googlepay.launcher.IGooglePayWebLauncher
import com.kttipay.payment.ui.LocalWebPaymentManager

/**
 * Returns a GooglePayWebLauncher for web platforms.
 *
 * Requires LocalWebPaymentConfig to be provided via CompositionLocalProvider.
 * Use PaymentManagerProvider to automatically provide both manager and config.
 *
 * Usage:
 * ```kotlin
 * val googlePayLauncher = rememberGooglePayWebLauncher(
 *     onResult = { result ->
 *         when (result) {
 *             is GooglePayWebResult.Success -> println("Token: ${result.token}")
 *             is GooglePayWebResult.Error -> println("Error: ${result.message}")
 *             GooglePayWebResult.Cancelled -> println("Cancelled")
 *         }
 *     }
 * )
 *
 * // Launch payment
 * Button(onClick = { googlePayLauncher?.launch("100.00") }) {
 *     Text("Pay with Google Pay")
 * }
 * ```
 *
 * @param onResult Callback for payment result
 * @return GooglePayWebLauncher instance or null if not configured
 */
@Composable
fun rememberGooglePayWebLauncher(
    onResult: (GooglePayWebResult) -> Unit
): IGooglePayWebLauncher {
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
