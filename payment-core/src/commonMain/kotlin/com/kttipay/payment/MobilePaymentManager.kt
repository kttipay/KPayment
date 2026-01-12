package com.kttipay.payment

import com.kttipay.payment.api.config.MobilePaymentConfig

/**
 * Type-safe typealias for managing mobile payments (Android/iOS).
 *
 * This simplifies type usage by eliminating the need for explicit generic type parameters.
 *
 * Use platform-specific factory functions to create instances:
 * - Android: `createMobilePaymentManager(config, context, scope)`
 * - iOS: `createMobilePaymentManager(config, scope)`
 *
 * Example usage:
 * ```kotlin
 * val manager: MobilePaymentManager = createMobilePaymentManager(config, context)
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
 * @see MobilePaymentConfig
 */
typealias MobilePaymentManager = PaymentManager<MobilePaymentConfig>
