package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.kttipay.payment.PaymentManager
import com.kttipay.payment.api.config.MobilePaymentConfig

/**
 * Convenience composable that provides both PaymentManager and MobilePaymentConfig
 * through composition locals.
 *
 * This helper wraps your content and makes both the payment manager and its configuration
 * available to child composables via LocalMobilePaymentManager and LocalMobilePaymentConfig.
 *
 * Usage:
 * ```kotlin
 * @Composable
 * fun App() {
 *     val config = remember {
 *         MobilePaymentConfig(
 *             googlePay = GooglePayConfig(...),
 *             applePayMobile = ApplePayMobileConfig(...),
 *             environment = PaymentEnvironment.Production
 *         )
 *     }
 *
 *     PaymentManagerProvider(config = config) {
 *         PaymentScreen()
 *     }
 * }
 * ```
 *
 * @param config The mobile payment configuration
 * @param content The composable content that will have access to the payment manager and config
 */
@Composable
fun PaymentManagerProvider(
    manager: PaymentManager<MobilePaymentConfig>,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalMobilePaymentManager provides manager) {
        content()
    }
}
