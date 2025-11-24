package com.kttipay.payment.internal.capability

import com.kttipay.payment.api.config.MobilePaymentConfig
import com.kttipay.payment.capability.CapabilityStatus

/**
 * Interface for checking payment provider availability on the current platform.
 *
 * Implementations are platform-specific (Android/iOS) and check if Google Pay
 * or Apple Pay are available and ready to use.
 */
interface CapabilityChecker {
    /**
     * Checks if Google Pay is available and ready to use.
     *
     * @param config Mobile payment configuration
     * @return Status indicating if Google Pay is ready, not configured, not supported, or has errors
     */
    suspend fun checkGooglePayAvailability(config: MobilePaymentConfig): CapabilityStatus

    /**
     * Checks if Apple Pay is available and ready to use.
     *
     * @param config Mobile payment configuration
     * @return Status indicating if Apple Pay is ready, not configured, not supported, or has errors
     */
    suspend fun checkApplePayAvailability(config: MobilePaymentConfig): CapabilityStatus
}