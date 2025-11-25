package com.kttipay.payment

import com.kttipay.payment.api.config.WebPaymentConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Web-specific factory that creates a WebPaymentManager with the given configuration.
 *
 * The manager is configured at construction time and capabilities are checked
 * lazily when the flow is first collected.
 *
 * Example usage:
 * ```kotlin
 * val config = WebPaymentConfig(
 *     googlePay = googlePayConfig,
 *     applePayWeb = applePayWebConfig,
 *     environment = PaymentEnvironment.Production
 * )
 * val paymentManager = createWebPaymentManager(config)
 * ```
 *
 * @param config The web payment configuration (Google Pay and/or Apple Pay)
 * @param scope CoroutineScope for async operations (defaults to Main dispatcher with SupervisorJob)
 * @return Configured WebPaymentManager instance
 */
fun createWebPaymentManager(
    config: WebPaymentConfig,
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
): WebPaymentManager {
    return WebPaymentManagerImpl(
        config = config,
        scope = scope
    )
}
