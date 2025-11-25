package com.kttipay.payment.ui.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kttipay.payment.api.config.toGooglePayWebConfig
import com.kttipay.payment.internal.googlepay.GooglePayWebLauncherFactory
import com.kttipay.payment.internal.googlepay.GooglePayWebResult
import com.kttipay.payment.internal.googlepay.launcher.IGooglePayWebLauncher
import com.kttipay.payment.ui.LocalWebPaymentConfig

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
 * Button(onClick = { googlePayLauncher?.launch(Deci(100)) }) {
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
): IGooglePayWebLauncher? {
    val config = LocalWebPaymentConfig.current

    val googlePayConfig = remember(config) {
        config.googlePay?.toGooglePayWebConfig(config.environment)
    }

    return remember(googlePayConfig, onResult) {
        googlePayConfig?.let {
            GooglePayWebLauncherFactory().create(
                config = it,
                onResult = onResult
            )
        }
    }
}
