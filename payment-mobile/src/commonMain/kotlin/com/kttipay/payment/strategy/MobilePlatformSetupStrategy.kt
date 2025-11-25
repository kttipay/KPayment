package com.kttipay.payment.strategy

import com.kttipay.payment.api.config.MobilePaymentConfig
import com.kttipay.payment.api.config.PlatformPaymentConfig
import com.kttipay.payment.internal.setup.PlatformSetup

/**
 * Mobile implementation of PlatformSetupStrategy.
 *
 * Delegates to the existing PlatformSetup interface with
 * platform-specific implementations (AndroidPlatformSetup, IosPlatformSetup).
 */
internal class MobilePlatformSetupStrategy(
    private val platformSetup: PlatformSetup
) : PlatformSetupStrategy {

    override fun setupPlatformPayments(config: PlatformPaymentConfig) {
        require(config is MobilePaymentConfig) {
            "MobilePlatformSetupStrategy requires MobilePaymentConfig"
        }
        platformSetup.setupPlatformPayments(config)
    }
}
