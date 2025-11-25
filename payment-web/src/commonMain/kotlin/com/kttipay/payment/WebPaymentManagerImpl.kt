package com.kttipay.payment

import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.config.ApplePayWebConfig
import com.kttipay.payment.api.config.GooglePayWebConfig
import com.kttipay.payment.api.config.WebPaymentConfig
import com.kttipay.payment.api.config.toGooglePayWebConfig
import com.kttipay.payment.capability.CapabilityStatus
import com.kttipay.payment.capability.PaymentCapabilities
import com.kttipay.payment.internal.capability.checkApplePayAvailability
import com.kttipay.payment.internal.capability.checkGooglePayAvailability
import com.kttipay.payment.internal.logging.KPaymentLogger
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
 * Default implementation of WebPaymentManager.
 *
 * This class manages payment provider configuration and capability checking
 * for web platforms.
 *
 * Payment capabilities are checked lazily when [capabilitiesFlow] is first collected.
 * Configuration is provided at construction time.
 *
 * @param config The web payment configuration (Google Pay and/or Apple Pay)
 * @param scope CoroutineScope for async capability checking
 */
class WebPaymentManagerImpl(
    private val config: WebPaymentConfig,
    private val scope: CoroutineScope
) : WebPaymentManager {

    private val googlePayConfigCache: GooglePayWebConfig? =
        config.googlePay?.toGooglePayWebConfig(config.environment)

    init {
        KPaymentLogger.tag("WebPaymentManager")
            .d("Initializing Web Payment - Environment: ${config.environment.name}")
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
                KPaymentLogger.tag("WebPaymentManager").e("Capability check failed", e)
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
            applePay = if (config.applePayWeb != null) {
                CapabilityStatus.Error("Capability check failed", error)
            } else {
                CapabilityStatus.NotConfigured
            }
        )
        _capabilitiesFlow.value = capabilities
    }

    override suspend fun refreshCapabilities(): PaymentCapabilities {
        KPaymentLogger.tag("WebPaymentManager").d("Refreshing payment capabilities")
        return refreshCapabilitiesInternal()
    }

    override fun canUse(provider: PaymentProvider): Boolean = capabilities.canPayWith(provider)

    override fun currentCapabilities(): PaymentCapabilities = capabilities

    override fun observeAvailability(provider: PaymentProvider): Flow<Boolean> =
        capabilitiesFlow
            .map { it.canPayWith(provider) }
            .distinctUntilChanged()

    override fun applePayConfig(): ApplePayWebConfig? = config.applePayWeb

    override fun googlePayConfig(): GooglePayWebConfig? = googlePayConfigCache

    private suspend fun refreshCapabilitiesInternal(): PaymentCapabilities {
        val appleStatus = try {
            config.applePayWeb?.let { checkApplePayAvailability(it) }
                ?: CapabilityStatus.NotConfigured
        } catch (e: Exception) {
            CapabilityStatus.Error("Apple Pay check failed", e)
        }

        val googleStatus = try {
            googlePayConfigCache?.let { checkGooglePayAvailability(it) }
                ?: CapabilityStatus.NotConfigured
        } catch (e: Exception) {
            CapabilityStatus.Error("Google Pay check failed", e)
        }

        capabilities = PaymentCapabilities(
            googlePay = googleStatus,
            applePay = appleStatus
        )
        _capabilitiesFlow.value = capabilities
        KPaymentLogger.tag("WebPaymentManager")
            .d("Capabilities — GooglePay: $googleStatus, ApplePay: $appleStatus")
        return capabilities
    }
}
