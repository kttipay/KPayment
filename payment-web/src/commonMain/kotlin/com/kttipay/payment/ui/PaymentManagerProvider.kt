package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.kttipay.payment.api.config.WebPaymentConfig

/**
 * Convenience composable that provides both PaymentManager and WebPaymentConfig
 * through composition locals.
 *
 * This helper wraps your content and makes both the payment manager and its configuration
 * available to child composables via LocalWebPaymentManager and LocalWebPaymentConfig.
 *
 * Usage:
 * ```kotlin
 * @Composable
 * fun App() {
 *     val config = remember {
 *         WebPaymentConfig(
 *             googlePay = GooglePayConfig(...),
 *             applePayWeb = ApplePayWebConfig(...),
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
 * @param config The web payment configuration
 * @param content The composable content that will have access to the payment manager and config
 */
@Composable
fun PaymentManagerProvider(
    config: WebPaymentConfig,
    content: @Composable () -> Unit
) {
    val manager = rememberWebPaymentManager(config)

    CompositionLocalProvider(
        LocalWebPaymentManager provides manager,
        LocalWebPaymentConfig provides config
    ) {
        content()
    }
}
