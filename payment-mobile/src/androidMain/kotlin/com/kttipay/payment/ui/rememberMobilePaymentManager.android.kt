package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.kttipay.payment.MobilePaymentManager
import com.kttipay.payment.api.config.MobilePaymentConfig
import com.kttipay.payment.createMobilePaymentManager

/**
 * Android implementation of rememberMobilePaymentManager.
 *
 * Creates and remembers a PaymentManager instance with the given configuration
 * using the Android-specific factory function. The instance is stable across
 * recompositions based on the config.
 *
 * @param config The payment configuration (Google Pay and/or Apple Pay)
 * @return A PaymentManager instance configured for Android
 */
@Composable
actual fun rememberMobilePaymentManager(config: MobilePaymentConfig): MobilePaymentManager {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    return remember(context, config) {
        createMobilePaymentManager(config, context, scope)
    }
}
