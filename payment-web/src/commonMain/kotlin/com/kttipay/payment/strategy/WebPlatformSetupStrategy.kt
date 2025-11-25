package com.kttipay.payment.strategy

import com.kttipay.payment.api.config.PlatformPaymentConfig

/**
 * Web implementation of PlatformSetupStrategy.
 *
 * No-op implementation as web platforms don't require setup.
 */
internal class WebPlatformSetupStrategy : PlatformSetupStrategy {
    override fun setupPlatformPayments(config: PlatformPaymentConfig) {
        // No setup needed for web
    }
}
