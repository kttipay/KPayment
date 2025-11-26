package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.kttipay.payment.PaymentManager
import com.kttipay.payment.api.config.MobilePaymentConfig
import com.kttipay.payment.createMobilePaymentManager

/**
 * iOS implementation of rememberMobilePaymentManager.
 *
 * Creates and remembers a PaymentManager instance with the given configuration
 * using the iOS-specific factory function. The instance is stable across
 * recompositions based on the config.
 *
 * @param config The payment configuration (Google Pay and/or Apple Pay)
 * @return A PaymentManager instance configured for iOS
 */
@Composable
actual fun rememberMobilePaymentManager(config: MobilePaymentConfig): PaymentManager<MobilePaymentConfig> {
    val scope = rememberCoroutineScope()
    return remember(config) {
        createMobilePaymentManager(config, scope)
    }
}
