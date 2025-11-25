package com.kttipay.payment.strategy

import com.kttipay.payment.api.config.PlatformPaymentConfig

/**
 * Strategy interface for platform-specific setup operations.
 *
 * Implementations handle any platform-specific initialization
 * required for payment providers.
 */
interface PlatformSetupStrategy {
    /**
     * Performs platform-specific setup for payment providers.
     *
     * @param config Platform payment configuration
     */
    fun setupPlatformPayments(config: PlatformPaymentConfig)
}
