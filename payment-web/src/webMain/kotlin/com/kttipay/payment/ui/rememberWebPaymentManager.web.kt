package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.kttipay.payment.PaymentManager
import com.kttipay.payment.api.config.WebPaymentConfig
import com.kttipay.payment.createWebPaymentManager

/**
 * Web implementation of rememberWebPaymentManager.
 *
 * Creates and remembers a PaymentManager instance with the given configuration
 * using the web-specific factory function. The instance is stable across
 * recompositions based on the config.
 *
 * @param config The payment configuration (Google Pay and/or Apple Pay)
 * @return A PaymentManager instance configured for web
 */
@Composable
actual fun rememberWebPaymentManager(config: WebPaymentConfig): PaymentManager {
    val scope = rememberCoroutineScope()
    return remember(config) {
        createWebPaymentManager(config, scope)
    }
}
