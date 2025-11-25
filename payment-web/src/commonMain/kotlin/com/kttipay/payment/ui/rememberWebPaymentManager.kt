package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import com.kttipay.payment.WebPaymentManager
import com.kttipay.payment.api.config.WebPaymentConfig

/**
 * Creates and remembers a WebPaymentManager instance with the given configuration.
 *
 * This is a platform-specific composable that automatically creates the appropriate
 * WebPaymentManager implementation for web platforms. The instance is remembered
 * across recompositions based on the config.
 *
 * The manager is configured at construction time and capabilities are checked
 * lazily when the flow is first collected.
 *
 * Example usage:
 * ```kotlin
 * @Composable
 * fun App() {
 *     val config = remember {
 *         WebPaymentConfig(
 *             googlePay = GooglePayConfig(...),
 *             applePayWeb = ApplePayWebConfig(...),
 *             environment = PaymentEnvironment.Production
 *         )
 *     }
 *
 *     val paymentManager = rememberWebPaymentManager(config)
 *
 *     // Provide to child composables
 *     CompositionLocalProvider(LocalWebPaymentManager provides paymentManager) {
 *         PaymentScreen()
 *     }
 * }
 * ```
 *
 * Note: Web implementation uses rememberCoroutineScope() for async operations.
 *
 * @param config The payment configuration (Google Pay and/or Apple Pay)
 * @return A platform-specific WebPaymentManager instance
 */
@Composable
expect fun rememberWebPaymentManager(config: WebPaymentConfig): WebPaymentManager
