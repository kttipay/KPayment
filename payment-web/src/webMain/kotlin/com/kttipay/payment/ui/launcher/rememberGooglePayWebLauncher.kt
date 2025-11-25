package com.kttipay.payment.ui.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.kttipay.payment.internal.googlepay.GooglePayWebLauncherFactory
import com.kttipay.payment.internal.googlepay.GooglePayWebResult
import com.kttipay.payment.internal.googlepay.launcher.IGooglePayWebLauncher
import com.kttipay.payment.internal.utils.ScriptLoader
import com.kttipay.payment.ui.LocalWebPaymentManager
import kotlin.js.ExperimentalWasmJsInterop

/**
 * Creates and remembers a Google Pay Web launcher.
 *
 * This composable automatically accesses the WebPaymentManager from the composition
 * to retrieve Google Pay configuration. If Google Pay is not configured, returns null.
 *
 * Example usage:
 * ```kotlin
 * @Composable
 * fun PaymentScreen() {
 *     val googlePayLauncher = rememberGooglePayWebLauncher { result ->
 *         when (result) {
 *             is GooglePayWebResult.Success -> handleSuccess(result.paymentData)
 *             is GooglePayWebResult.Canceled -> handleCanceled()
 *             is GooglePayWebResult.Error -> handleError(result.message)
 *         }
 *     }
 *
 *     googlePayLauncher?.launch(amount = Deci("99.99"))
 * }
 * ```
 *
 * @param onResult Callback invoked when the Google Pay flow completes
 * @return A Google Pay launcher instance, or null if Google Pay is not configured
 */
@OptIn(ExperimentalWasmJsInterop::class)
@Composable
fun rememberGooglePayWebLauncher(
    onResult: (GooglePayWebResult) -> Unit
): IGooglePayWebLauncher? {
    val paymentManager = LocalWebPaymentManager.current
    val config = remember(paymentManager) {
        paymentManager.googlePayConfig()
    }
    LaunchedEffect(Unit) {
        ScriptLoader.loadGooglePayScript()
    }
    return config?.let {
        remember(config, onResult) {
            GooglePayWebLauncherFactory().create(config, onResult)
        }
    }
}
