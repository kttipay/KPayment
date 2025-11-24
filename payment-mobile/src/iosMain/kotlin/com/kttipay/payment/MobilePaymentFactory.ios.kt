package com.kttipay.payment

import com.kttipay.payment.api.config.MobilePaymentConfig
import com.kttipay.payment.internal.capability.IosCapabilityChecker
import com.kttipay.payment.internal.setup.IosPlatformSetup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * iOS-specific factory that creates a MobilePaymentManager with the given configuration.
 *
 * The manager is configured at construction time and capabilities are checked
 * lazily when the flow is first collected.
 *
 * Example usage:
 * ```kotlin
 * val config = MobilePaymentConfig(
 *     googlePay = null,
 *     applePayMobile = applePayConfig,
 *     environment = PaymentEnvironment.Production
 * )
 * val paymentManager = createMobilePaymentManager(config)
 * ```
 *
 * @param config The payment configuration (Google Pay and/or Apple Pay)
 * @param scope CoroutineScope for async operations (defaults to Main dispatcher with SupervisorJob)
 */
fun createMobilePaymentManager(
    config: MobilePaymentConfig,
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
): MobilePaymentManager {
    return MobilePaymentManagerImpl(
        config = config,
        capabilityChecker = IosCapabilityChecker(),
        platformSetup = IosPlatformSetup(),
        scope = scope
    )
}
