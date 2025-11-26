package com.kttipay.payment

import com.kttipay.payment.api.config.WebPaymentConfig

/**
 * Type-safe typealias for managing web payments.
 *
 * This simplifies type usage by eliminating the need for explicit generic type parameters.
 *
 * Use the factory function to create instances:
 * - `createWebPaymentManager(config, scope)`
 *
 * Example usage:
 * ```kotlin
 * // Before: PaymentManager<WebPaymentConfig>
 * val manager: WebPaymentManager = createWebPaymentManager(config)
 *
 * // Composable usage
 * @Composable
 * fun MyPaymentScreen() {
 *     val manager = LocalWebPaymentManager.current
 *     val capabilities by manager.capabilitiesFlow.collectAsState()
 * }
 * ```
 *
 * @see PaymentManager
 * @see WebPaymentConfig
 */
typealias WebPaymentManager = PaymentManager<WebPaymentConfig>
