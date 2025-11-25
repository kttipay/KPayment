package com.kttipay.payment

import android.content.Context
import com.kttipay.payment.api.config.MobilePaymentConfig
import com.kttipay.payment.internal.capability.AndroidCapabilityChecker
import com.kttipay.payment.internal.googlepay.GooglePayService
import com.kttipay.payment.internal.googlepay.GooglePayServiceImpl
import com.kttipay.payment.internal.setup.AndroidPlatformSetup
import com.kttipay.payment.strategy.MobileCapabilityCheckStrategy
import com.kttipay.payment.strategy.MobileConfigAccessor
import com.kttipay.payment.strategy.MobilePlatformSetupStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

private val sharedGooglePayService = GooglePayServiceImpl()

/**
 * Android-specific factory that creates a PaymentManager with the given configuration.
 *
 * The manager is configured at construction time and capabilities are checked
 * lazily when the flow is first collected.
 *
 * Example usage:
 * ```kotlin
 * val context = applicationContext
 * val config = MobilePaymentConfig(
 *     googlePay = googlePayConfig,
 *     applePayMobile = null,
 *     environment = PaymentEnvironment.Production
 * )
 * val paymentManager = createMobilePaymentManager(config, context)
 * ```
 *
 * @param config The payment configuration (Google Pay and/or Apple Pay)
 * @param context Android application context
 * @param scope CoroutineScope for async operations (defaults to Main dispatcher with SupervisorJob)
 */
fun createMobilePaymentManager(
    config: MobilePaymentConfig,
    context: Context,
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
): PaymentManager {
    val capabilityChecker = AndroidCapabilityChecker(
        googlePayService = sharedGooglePayService,
        context = context.applicationContext
    )
    val platformSetup = AndroidPlatformSetup(sharedGooglePayService)

    return PaymentManagerImpl(
        config = config,
        capabilityCheckStrategy = MobileCapabilityCheckStrategy(capabilityChecker),
        platformSetupStrategy = MobilePlatformSetupStrategy(platformSetup),
        configAccessor = MobileConfigAccessor(),
        scope = scope,
        logTag = "MobilePaymentManager"
    )
}

/**
 * Returns the shared GooglePayService instance.
 *
 * This is the same instance used by MobilePaymentManager and all its dependencies.
 * It's exposed here to allow legacy code (GooglePayEnvironment) to access it.
 *
 * @return Shared GooglePayService instance
 */
internal fun getSharedGooglePayService(): GooglePayService = sharedGooglePayService