package com.kttipay.payment.strategy

import com.kttipay.payment.api.config.ApplePayConfig
import com.kttipay.payment.api.config.GooglePayConfig
import com.kttipay.payment.api.config.PlatformPaymentConfig

/**
 * Strategy interface for platform-specific config access.
 *
 * Handles platform differences in how configurations are accessed,
 * including any necessary transformations or caching.
 */
interface ConfigAccessor {
    /**
     * Returns the Apple Pay configuration.
     *
     * @param config Platform payment configuration
     * @return Apple Pay config or null if not configured
     */
    fun getApplePayConfig(config: PlatformPaymentConfig): ApplePayConfig?

    /**
     * Returns the Google Pay configuration.
     *
     * Note: Web platforms may need to transform this to GooglePayWebConfig.
     *
     * @param config Platform payment configuration
     * @return Google Pay config or null if not configured
     */
    fun getGooglePayConfig(config: PlatformPaymentConfig): GooglePayConfig?
}
