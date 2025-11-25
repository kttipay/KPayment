package com.kttipay.payment.strategy

import com.kttipay.payment.api.config.PlatformPaymentConfig
import com.kttipay.payment.capability.CapabilityStatus

/**
 * Strategy interface for platform-specific capability checking.
 *
 * Implementations provide platform-specific logic to check if
 * Google Pay or Apple Pay are available and ready to use.
 */
interface CapabilityCheckStrategy {
    /**
     * Checks if Google Pay is available and ready.
     *
     * @param config Platform payment configuration
     * @return Status indicating Google Pay availability
     */
    suspend fun checkGooglePayAvailability(config: PlatformPaymentConfig): CapabilityStatus

    /**
     * Checks if Apple Pay is available and ready.
     *
     * @param config Platform payment configuration
     * @return Status indicating Apple Pay availability
     */
    suspend fun checkApplePayAvailability(config: PlatformPaymentConfig): CapabilityStatus
}
