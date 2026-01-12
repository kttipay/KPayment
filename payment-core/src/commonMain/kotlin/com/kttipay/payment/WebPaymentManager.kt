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
 * val manager: WebPaymentManager = createWebPaymentManager(config)
 *
 * // Check capabilities
 * val capabilities = manager.checkCapabilities()
 *
 * // Reactive UI observation
 * val isReady by manager.observeAvailability(PaymentProvider.GooglePay)
 *     .collectAsState(initial = false)
 * ```
 *
 * @see PaymentManager
 * @see WebPaymentConfig
 */
typealias WebPaymentManager = PaymentManager<WebPaymentConfig>
