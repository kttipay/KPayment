package com.kttipay.payment.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.kttipay.payment.WebPaymentManager

/**
 * CompositionLocal for providing WebPaymentManager to Compose UI.
 *
 * This allows Compose functions to access the payment manager without
 * explicit parameter passing.
 *
 * Usage:
 * ```kotlin
 * // Provide at root level
 * CompositionLocalProvider(LocalWebPaymentManager provides paymentManager) {
 *     // Your app content
 * }
 *
 * // Access in composables
 * @Composable
 * fun MyPaymentScreen() {
 *     val paymentManager = LocalWebPaymentManager.current
 *     // Use paymentManager
 * }
 * ```
 */
val LocalWebPaymentManager = staticCompositionLocalOf<WebPaymentManager> {
    error(
        "No WebPaymentManager provided. Make sure to provide it using " +
        "CompositionLocalProvider(LocalWebPaymentManager provides paymentManager) " +
        "at the root of your Compose tree."
    )
}
