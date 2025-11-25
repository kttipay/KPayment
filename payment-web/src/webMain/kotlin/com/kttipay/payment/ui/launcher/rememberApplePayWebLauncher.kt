package com.kttipay.payment.ui.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kttipay.payment.internal.applepay.ApplePayWebResult
import com.kttipay.payment.internal.applepay.launcher.ApplePayWebLauncher
import com.kttipay.payment.internal.applepay.launcher.IApplePayWebLauncher
import com.kttipay.payment.ui.LocalWebPaymentManager

/**
 * Creates and remembers an Apple Pay Web launcher.
 *
 * This composable automatically accesses the WebPaymentManager from the composition
 * to retrieve Apple Pay configuration. If Apple Pay is not configured, returns null.
 *
 * Example usage:
 * ```kotlin
 * @Composable
 * fun PaymentScreen() {
 *     val applePayLauncher = rememberApplePayWebLauncher { result ->
 *         when (result) {
 *             is ApplePayWebResult.Success -> handleSuccess(result.token)
 *             is ApplePayWebResult.Canceled -> handleCanceled()
 *             is ApplePayWebResult.Error -> handleError(result.message)
 *         }
 *     }
 *
 *     applePayLauncher?.launch(amount = Deci("99.99"))
 * }
 * ```
 *
 * @param onResult Callback invoked when the Apple Pay flow completes
 * @return An Apple Pay launcher instance, or null if Apple Pay is not configured
 */
@Composable
fun rememberApplePayWebLauncher(
    onResult: (ApplePayWebResult) -> Unit
): IApplePayWebLauncher? {
    val paymentManager = LocalWebPaymentManager.current
    val config = remember(paymentManager) {
        paymentManager.applePayConfig()
    }
    return config?.let {
        remember(config, onResult) {
            ApplePayWebLauncher(config, onResult)
        }
    }
}
