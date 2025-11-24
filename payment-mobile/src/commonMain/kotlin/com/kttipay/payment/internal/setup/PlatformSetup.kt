package com.kttipay.payment.internal.setup

import com.kttipay.payment.api.config.MobilePaymentConfig

/**
 * Interface for platform-specific payment setup operations.
 *
 * Implementations handle any platform-specific initialization required
 * for payment providers (e.g., configuring Google Pay service on Android).
 */
interface PlatformSetup {
    /**
     * Performs platform-specific setup for payment providers.
     *
     * @param config Mobile payment configuration
     */
    fun setupPlatformPayments(config: MobilePaymentConfig)
}
