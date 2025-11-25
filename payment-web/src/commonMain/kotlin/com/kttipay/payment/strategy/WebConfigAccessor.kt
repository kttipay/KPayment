package com.kttipay.payment.strategy

import com.kttipay.payment.api.config.ApplePayConfig
import com.kttipay.payment.api.config.GooglePayConfig
import com.kttipay.payment.api.config.GooglePayWebConfig
import com.kttipay.payment.api.config.PlatformPaymentConfig
import com.kttipay.payment.api.config.WebPaymentConfig
import com.kttipay.payment.api.config.toGooglePayWebConfig

/**
 * Web implementation of ConfigAccessor.
 *
 * Provides access to web configurations with GooglePayWebConfig caching.
 */
internal class WebConfigAccessor(
    config: PlatformPaymentConfig
) : ConfigAccessor {

    // Cache GooglePayWebConfig transformation
    private val googlePayConfigCache: GooglePayWebConfig? = run {
        require(config is WebPaymentConfig) {
            "WebConfigAccessor requires WebPaymentConfig"
        }
        config.googlePay?.toGooglePayWebConfig(config.environment)
    }

    override fun getApplePayConfig(config: PlatformPaymentConfig): ApplePayConfig? {
        require(config is WebPaymentConfig) {
            "WebConfigAccessor requires WebPaymentConfig"
        }
        return config.applePayWeb
    }

    override fun getGooglePayConfig(config: PlatformPaymentConfig): GooglePayConfig? {
        return config.googlePay
    }

    /**
     * Web-specific method to get the transformed GooglePayWebConfig.
     * This is used by web-specific UI components.
     */
    fun getGooglePayWebConfig(): GooglePayWebConfig? = googlePayConfigCache
}
