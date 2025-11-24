package com.kttipay.payment.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.kttipay.payment.MobilePaymentManager

/**
 * CompositionLocal for providing MobilePaymentManager to Compose UI.
 *
 * This allows Compose functions to access the payment manager without
 * explicit parameter passing.
 *
 * Usage:
 * ```kotlin
 * // Provide at root level
 * CompositionLocalProvider(LocalMobilePaymentManager provides paymentManager) {
 *     // Your app content
 * }
 *
 * // Access in composables
 * @Composable
 * fun MyPaymentScreen() {
 *     val paymentManager = LocalMobilePaymentManager.current
 *     // Use paymentManager
 * }
 * ```
 */
val LocalMobilePaymentManager = staticCompositionLocalOf<MobilePaymentManager> {
    error(
        "No MobilePaymentManager provided. Make sure to provide it using " +
        "CompositionLocalProvider(LocalMobilePaymentManager provides paymentManager) " +
        "at the root of your Compose tree."
    )
}
