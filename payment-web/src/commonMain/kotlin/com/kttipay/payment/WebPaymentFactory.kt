package com.kttipay.payment

import com.kttipay.payment.api.config.WebPaymentConfig
import com.kttipay.payment.strategy.WebCapabilityCheckStrategy
import com.kttipay.payment.strategy.WebConfigAccessor
import com.kttipay.payment.strategy.WebPlatformSetupStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Web-specific factory that creates a PaymentManager with the given configuration.
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
 * @return Configured PaymentManager instance
 */
fun createWebPaymentManager(
    config: WebPaymentConfig,
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
): PaymentManager<WebPaymentConfig> {
    return PaymentManagerImpl(
        config = config,
        capabilityCheckStrategy = WebCapabilityCheckStrategy(),
        platformSetupStrategy = WebPlatformSetupStrategy(),
        configAccessor = WebConfigAccessor(config),
        scope = scope,
        logTag = "WebPaymentManager"
    )
}
