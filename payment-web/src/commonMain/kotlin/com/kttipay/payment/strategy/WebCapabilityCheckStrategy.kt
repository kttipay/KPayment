package com.kttipay.payment.strategy

import com.kttipay.payment.api.config.ApplePayWebConfig
import com.kttipay.payment.api.config.GooglePayWebConfig
import com.kttipay.payment.api.config.PlatformPaymentConfig
import com.kttipay.payment.api.config.WebPaymentConfig
import com.kttipay.payment.api.config.toGooglePayWebConfig
import com.kttipay.payment.capability.CapabilityStatus
import com.kttipay.payment.internal.capability.checkApplePayAvailability
import com.kttipay.payment.internal.capability.checkGooglePayAvailability

/**
 * Web implementation of CapabilityCheckStrategy.
 *
 * Uses standalone expect/actual functions for capability checking.
 */
internal class WebCapabilityCheckStrategy : CapabilityCheckStrategy {

    override suspend fun checkGooglePayAvailability(
        config: PlatformPaymentConfig
    ): CapabilityStatus {
        require(config is WebPaymentConfig) {
            "WebCapabilityCheckStrategy requires WebPaymentConfig"
        }

        return try {
            val webConfig: GooglePayWebConfig? = config.googlePay
                ?.toGooglePayWebConfig(config.environment)

            webConfig?.let { checkGooglePayAvailability(it) }
                ?: CapabilityStatus.NotConfigured
        } catch (e: Exception) {
            CapabilityStatus.Error("Google Pay check failed", e)
        }
    }

    override suspend fun checkApplePayAvailability(
        config: PlatformPaymentConfig
    ): CapabilityStatus {
        require(config is WebPaymentConfig) {
            "WebCapabilityCheckStrategy requires WebPaymentConfig"
        }

        return try {
            val appleConfig: ApplePayWebConfig? = config.applePayWeb

            appleConfig?.let { checkApplePayAvailability(it) }
                ?: CapabilityStatus.NotConfigured
        } catch (e: Exception) {
            CapabilityStatus.Error("Apple Pay check failed", e)
        }
    }
}
