package com.kttipay.payment

import com.kttipay.payment.api.PaymentEnvironment
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.config.ApplePayMobileConfig
import com.kttipay.payment.api.config.GooglePayConfig
import com.kttipay.payment.api.config.MobilePaymentConfig
import com.kttipay.payment.capability.CapabilityStatus
import com.kttipay.payment.capability.PaymentCapabilities
import com.kttipay.payment.internal.capability.CapabilityChecker
import com.kttipay.payment.internal.setup.PlatformSetup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.kimplify.cedar.logging.Cedar

/**
 * Default implementation of MobilePaymentManager.
 *
 * This class uses dependency injection to accept platform-specific implementations
 * of capability checking, platform setup, and validation.
 *
 * Payment capabilities are checked lazily when [capabilitiesFlow] is first collected.
 * Configuration is validated and platform setup is performed synchronously at construction time.
 *
 * @param config The payment configuration (Google Pay and/or Apple Pay)
 * @param capabilityChecker Checks if payment providers are available
 * @param platformSetup Performs platform-specific payment setup
 * @param platformValidator Validates platform requirements at construction
 * @param scope CoroutineScope for async capability checking
 */
class MobilePaymentManagerImpl(
    private val config: MobilePaymentConfig,
    private val capabilityChecker: CapabilityChecker,
    private val platformSetup: PlatformSetup,
    private val scope: CoroutineScope
) : MobilePaymentManager {

    init {
        Cedar.tag("MobilePaymentManager")
            .d("Initializing Mobile Payment - Environment: ${config.environment.name}")

        platformSetup.setupPlatformPayments(config)
    }

    private var capabilities: PaymentCapabilities = PaymentCapabilities(
        googlePay = CapabilityStatus.NotConfigured,
        applePay = CapabilityStatus.NotConfigured
    )

    private val _capabilitiesFlow = MutableStateFlow(capabilities)
    private var hasCheckedCapabilities = false

    override val capabilitiesFlow: StateFlow<PaymentCapabilities> =
        _capabilitiesFlow
            .onStart { checkCapabilitiesOnFirstCollection() }
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), capabilities)

    private fun checkCapabilitiesOnFirstCollection() {
        if (hasCheckedCapabilities) return

        hasCheckedCapabilities = true
        scope.launch {
            try {
                refreshCapabilitiesInternal()
            } catch (e: Exception) {
                Cedar.tag("MobilePaymentManager").e("Capability check failed", e)
                handleCapabilityCheckFailure(e)
            }
        }
    }

    private fun handleCapabilityCheckFailure(error: Exception) {
        capabilities = PaymentCapabilities(
            googlePay = if (config.googlePay != null) {
                CapabilityStatus.Error("Capability check failed", error)
            } else {
                CapabilityStatus.NotConfigured
            },
            applePay = if (config.applePayMobile != null) {
                CapabilityStatus.Error("Capability check failed", error)
            } else {
                CapabilityStatus.NotConfigured
            }
        )
        _capabilitiesFlow.value = capabilities
    }

    override suspend fun refreshCapabilities(): PaymentCapabilities {
        Cedar.tag("MobilePaymentManager").d("Refreshing payment capabilities")
        return refreshCapabilitiesInternal()
    }

    override fun canUse(provider: PaymentProvider): Boolean = capabilities.canPayWith(provider)

    override fun currentCapabilities(): PaymentCapabilities = capabilities

    override fun observeAvailability(provider: PaymentProvider): Flow<Boolean> =
        capabilitiesFlow
            .map { it.canPayWith(provider) }
            .distinctUntilChanged()

    override fun applePayConfig(): ApplePayMobileConfig? = config.applePayMobile

    private suspend fun refreshCapabilitiesInternal(): PaymentCapabilities {
        val googleStatus = capabilityChecker.checkGooglePayAvailability(config)
        val appleStatus = capabilityChecker.checkApplePayAvailability(config)

        capabilities = PaymentCapabilities(
            googlePay = googleStatus,
            applePay = appleStatus
        )
        _capabilitiesFlow.value = capabilities
        Cedar.tag("MobilePaymentManager")
            .d("Capabilities — GooglePay: $googleStatus, ApplePay: $appleStatus")
        return capabilities
    }
}

/**
 * DSL Builder for constructing MobilePaymentConfig.
 */
class PaymentConfigBuilder(private val environment: PaymentEnvironment) {
    private var googlePayConfig: GooglePayConfig? = null
    private var applePayConfig: ApplePayMobileConfig? = null

    fun googlePay(value: GooglePayConfig) = apply { googlePayConfig = value }

    fun applePay(value: ApplePayMobileConfig) = apply { applePayConfig = value }

    fun build(): MobilePaymentConfig {

        return MobilePaymentConfig(
            environment = environment,
            googlePay = googlePayConfig,
            applePayMobile = applePayConfig
        )
    }
}