package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.kttipay.payment.WebPaymentManager
import com.kttipay.payment.api.config.WebPaymentConfig
import com.kttipay.payment.createWebPaymentManager

/**
 * Android implementation of rememberWebPaymentManager (stub for KMP support).
 *
 * This exists for Kotlin Multiplatform compilation purposes but should not
 * be used at runtime. Web payment features are only available on actual web targets.
 *
 * @param config The payment configuration (Google Pay and/or Apple Pay)
 * @return A PaymentManager instance (stub implementation)
 */
@Composable
actual fun rememberWebPaymentManager(config: WebPaymentConfig): WebPaymentManager {
    val scope = rememberCoroutineScope()
    return remember(config) {
        createWebPaymentManager(config, scope)
    }
}
