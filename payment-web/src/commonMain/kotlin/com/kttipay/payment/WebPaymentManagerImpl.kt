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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Default implementation of [WebPaymentManager].
 *
 * This class manages payment provider initialization and capability checking
 * for web platforms.
 *
 * @see WebPaymentManager
 */
class WebPaymentManagerImpl : WebPaymentManager {

    private val _capabilities = MutableStateFlow(PaymentCapabilities.Uninitialized)
    override val capabilities: StateFlow<PaymentCapabilities> = _capabilities.asStateFlow()

    private var activeConfig: WebPaymentConfig? = null
    private var googlePayConfigCache: GooglePayWebConfig? = null

    override fun initialize(config: WebPaymentConfig) {
        activeConfig = config
        googlePayConfigCache = config.googlePay?.toGooglePayWebConfig(config.environment)

        val checkingCapabilities = PaymentCapabilities(
            googlePay = if (config.googlePay != null) CapabilityStatus.Checking else CapabilityStatus.NotConfigured,
            applePay = if (config.applePayWeb != null) CapabilityStatus.Checking else CapabilityStatus.NotConfigured
        )
        _capabilities.value = checkingCapabilities
    }

    override suspend fun checkCapabilities() {
        check(isInitialized()) { "WebPaymentManager must be initialized before checking capabilities" }

        val config = activeConfig ?: return

        val appleStatus = try {
            config.applePayWeb?.let { checkApplePayAvailability(it) }
                ?: CapabilityStatus.NotConfigured
        } catch (e: Exception) {
            CapabilityStatus.Error("Apple Pay check failed: ${e.message}")
        }

        val googleStatus = try {
            googlePayConfigCache?.let { checkGooglePayAvailability(it) }
                ?: CapabilityStatus.NotConfigured
        } catch (e: Exception) {
            CapabilityStatus.Error("Google Pay check failed: ${e.message}")
        }

        val finalCapabilities = PaymentCapabilities(
            googlePay = googleStatus,
            applePay = appleStatus
        )

        _capabilities.value = finalCapabilities
    }

    override fun applePayConfig(): ApplePayWebConfig {
        return activeConfig?.applePayWeb
            ?: error("Apple Pay is not configured. Call initialize() with ApplePayWebConfig first.")
    }

    override fun googlePayConfig(): GooglePayWebConfig {
        return googlePayConfigCache
            ?: error("Google Pay is not configured. Call initialize() with GooglePayConfig first.")
    }

    override fun canUse(provider: PaymentProvider): Boolean {
        return _capabilities.value.canPayWith(provider)
    }

    override fun currentCapabilities(): PaymentCapabilities {
        return _capabilities.value
    }

    override fun isInitialized(): Boolean {
        return activeConfig != null
    }
}
