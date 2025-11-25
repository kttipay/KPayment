package com.kttipay.payment.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.kttipay.payment.api.config.WebPaymentConfig

/**
 * Composition local for web payment configuration.
 *
 * Provides access to platform-specific payment configs (ApplePay, GooglePay)
 * that payment launchers need to initialize.
 *
 * Usage:
 * ```kotlin
 * CompositionLocalProvider(
 *     LocalWebPaymentManager provides paymentManager,
 *     LocalWebPaymentConfig provides config
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
val LocalWebPaymentConfig = staticCompositionLocalOf<WebPaymentConfig> {
    error("LocalWebPaymentConfig not provided. Wrap your composables with PaymentManagerProvider or provide LocalWebPaymentConfig.")
}
