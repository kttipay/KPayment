package com.kttipay.payment.strategy

import com.kttipay.payment.api.config.MobilePaymentConfig
import com.kttipay.payment.api.config.PlatformPaymentConfig
import com.kttipay.payment.capability.CapabilityStatus
import com.kttipay.payment.internal.capability.CapabilityChecker

/**
 * Mobile implementation of CapabilityCheckStrategy.
 *
 * Delegates to the existing CapabilityChecker interface with
 * platform-specific implementations (AndroidCapabilityChecker, IosCapabilityChecker).
 */
internal class MobileCapabilityCheckStrategy(
    private val capabilityChecker: CapabilityChecker
) : CapabilityCheckStrategy {

    override suspend fun checkGooglePayAvailability(
        config: PlatformPaymentConfig
    ): CapabilityStatus {
        require(config is MobilePaymentConfig) {
            "MobileCapabilityCheckStrategy requires MobilePaymentConfig"
        }
        return capabilityChecker.checkGooglePayAvailability(config)
    }

    override suspend fun checkApplePayAvailability(
        config: PlatformPaymentConfig
    ): CapabilityStatus {
        require(config is MobilePaymentConfig) {
            "MobileCapabilityCheckStrategy requires MobilePaymentConfig"
        }
        return capabilityChecker.checkApplePayAvailability(config)
    }
}
