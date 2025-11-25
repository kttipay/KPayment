package com.kttipay.payment.ui.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kttipay.payment.internal.applepay.ApplePayWebResult
import com.kttipay.payment.internal.applepay.launcher.ApplePayWebLauncher
import com.kttipay.payment.internal.applepay.launcher.IApplePayWebLauncher
import com.kttipay.payment.ui.LocalWebPaymentConfig

/**
 * Returns an ApplePayWebLauncher for web platforms.
 *
 * Requires LocalWebPaymentConfig to be provided via CompositionLocalProvider.
 * Use PaymentManagerProvider to automatically provide both manager and config.
 *
 * Usage:
 * ```kotlin
 * val applePayLauncher = rememberApplePayWebLauncher(
 *     onResult = { result ->
 *         when (result) {
 *             is ApplePayWebResult.Success -> println("Token: ${result.token}")
 *             ApplePayWebResult.Failure -> println("Failed")
 *             ApplePayWebResult.Cancelled -> println("Cancelled")
 *         }
 *     }
 * )
 *
 * // Launch payment
 * Button(onClick = { applePayLauncher?.launch(Deci(100)) }) {
 *     Text("Pay with Apple Pay")
 * }
 * ```
 *
 * @param onResult Callback for payment result
 * @return ApplePayWebLauncher instance or null if not configured
 */
@Composable
fun rememberApplePayWebLauncher(
    onResult: (ApplePayWebResult) -> Unit
): IApplePayWebLauncher? {
    val config = LocalWebPaymentConfig.current

    val applePayConfig = remember(config) {
        config.applePayWeb
    }

    return remember(applePayConfig, onResult) {
        applePayConfig?.let {
            ApplePayWebLauncher(
                config = it,
                onResult = onResult
            )
        }
    }
}
