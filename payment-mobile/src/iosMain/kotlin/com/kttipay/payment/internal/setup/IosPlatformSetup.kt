package com.kttipay.payment.internal.setup

import com.kttipay.payment.api.config.MobilePaymentConfig

/**
 * iOS implementation of PlatformSetup.
 *
 * No platform-specific setup required - Apple Pay uses KotlinNativeApplePayFactory
 * which is automatically instantiated when the payment launcher is created.
 */
internal class IosPlatformSetup : PlatformSetup {

    override fun setupPlatformPayments(config: MobilePaymentConfig) {
        // No setup needed - factory is lazily created in ApplePayPaymentLauncher
    }
}
