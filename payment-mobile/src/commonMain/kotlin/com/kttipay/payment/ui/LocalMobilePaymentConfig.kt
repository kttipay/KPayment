package com.kttipay.payment.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.kttipay.payment.api.config.MobilePaymentConfig

/**
 * Composition local for mobile payment configuration.
 *
 * Provides access to platform-specific payment configs (ApplePay, GooglePay)
 * that payment launchers need to initialize.
 *
 * Usage:
 * ```kotlin
 * CompositionLocalProvider(
 *     LocalMobilePaymentManager provides paymentManager,
 *     LocalMobilePaymentConfig provides config
 * ) {
 *     PaymentScreen()
 * }
 * ```
 *
 * Or use the PaymentManagerProvider helper:
 * ```kotlin
 * PaymentManagerProvider(config = config) {
 *     PaymentScreen()
 * }
 * ```
 */
val LocalMobilePaymentConfig = staticCompositionLocalOf<MobilePaymentConfig> {
    error("LocalMobilePaymentConfig not provided. Wrap your composables with PaymentManagerProvider or provide LocalMobilePaymentConfig.")
}
