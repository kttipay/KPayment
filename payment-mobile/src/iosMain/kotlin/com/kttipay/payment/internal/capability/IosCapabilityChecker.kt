package com.kttipay.payment.internal.capability

import com.kttipay.payment.api.config.MobilePaymentConfig
import com.kttipay.payment.capability.CapabilityStatus
import platform.PassKit.PKPaymentAuthorizationController

/**
 * iOS implementation of CapabilityChecker.
 *
 * Uses PassKit to check Apple Pay availability.
 * Google Pay is not supported on iOS.
 */
internal class IosCapabilityChecker : CapabilityChecker {

    override suspend fun checkGooglePayAvailability(
        config: MobilePaymentConfig
    ): CapabilityStatus = CapabilityStatus.NotSupported

    override suspend fun checkApplePayAvailability(
        config: MobilePaymentConfig
    ): CapabilityStatus {
        val appleConfig = config.applePayMobile ?: return CapabilityStatus.NotConfigured
        return if (PKPaymentAuthorizationController.canMakePayments()) {
            CapabilityStatus.Ready
        } else {
            CapabilityStatus.Error("Apple Pay not available for merchant ${appleConfig.base.merchantName}")
        }
    }
}