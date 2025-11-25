package com.kttipay.payment

import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.config.PlatformPaymentConfig
import com.kttipay.payment.capability.CapabilityStatus
import com.kttipay.payment.capability.PaymentCapabilities
import com.kttipay.payment.internal.logging.KPaymentLogger
import com.kttipay.payment.strategy.CapabilityCheckStrategy
import com.kttipay.payment.strategy.ConfigAccessor
import com.kttipay.payment.strategy.PlatformSetupStrategy
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

/**
 * Unified implementation of PaymentManager.
 *
 * This class uses Strategy pattern with DI to accept platform-specific implementations
 * of capability checking, platform setup, and configuration access.
 *
 * Payment capabilities are checked lazily when [capabilitiesFlow] is first collected.
 * Configuration is provided at construction time and platform setup is performed synchronously.
 *
 * @param config The platform payment configuration
 * @param capabilityCheckStrategy Strategy for checking payment provider availability
 * @param platformSetupStrategy Strategy for platform-specific setup
 * @param configAccessor Strategy for accessing platform-specific configurations
 * @param scope CoroutineScope for async capability checking
 * @param logTag Log tag for this manager instance (defaults to "PaymentManager")
 */
class PaymentManagerImpl(
    private val config: PlatformPaymentConfig,
    private val capabilityCheckStrategy: CapabilityCheckStrategy,
    private val platformSetupStrategy: PlatformSetupStrategy,
    private val configAccessor: ConfigAccessor,
    private val scope: CoroutineScope,
    private val logTag: String = "PaymentManager"
) : PaymentManager {

    init {
        KPaymentLogger.tag(logTag)
            .d("Initializing Payment Manager - Environment: ${config.environment.name}")

        platformSetupStrategy.setupPlatformPayments(config)
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
                KPaymentLogger.tag(logTag).e("Capability check failed", e)
                handleCapabilityCheckFailure(e)
            }
        }
    }

    private fun handleCapabilityCheckFailure(error: Exception) {
        capabilities = PaymentCapabilities(
            googlePay = if (configAccessor.getGooglePayConfig(config) != null) {
                CapabilityStatus.Error("Capability check failed", error)
            } else {
                CapabilityStatus.NotConfigured
            },
            applePay = if (configAccessor.getApplePayConfig(config) != null) {
                CapabilityStatus.Error("Capability check failed", error)
            } else {
                CapabilityStatus.NotConfigured
            }
        )
        _capabilitiesFlow.value = capabilities
    }

    override suspend fun refreshCapabilities(): PaymentCapabilities {
        KPaymentLogger.tag(logTag).d("Refreshing payment capabilities")
        return refreshCapabilitiesInternal()
    }

    override fun canUse(provider: PaymentProvider): Boolean = capabilities.canPayWith(provider)

    override fun currentCapabilities(): PaymentCapabilities = capabilities

    override fun observeAvailability(provider: PaymentProvider): Flow<Boolean> =
        capabilitiesFlow
            .map { it.canPayWith(provider) }
            .distinctUntilChanged()

    private suspend fun refreshCapabilitiesInternal(): PaymentCapabilities {
        val googleStatus = capabilityCheckStrategy.checkGooglePayAvailability(config)
        val appleStatus = capabilityCheckStrategy.checkApplePayAvailability(config)

        capabilities = PaymentCapabilities(
            googlePay = googleStatus,
            applePay = appleStatus
        )
        _capabilitiesFlow.value = capabilities
        KPaymentLogger.tag(logTag)
            .d("Capabilities — GooglePay: $googleStatus, ApplePay: $appleStatus")
        return capabilities
    }
}
