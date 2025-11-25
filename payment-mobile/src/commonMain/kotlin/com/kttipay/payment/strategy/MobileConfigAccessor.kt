package com.kttipay.payment.strategy

import com.kttipay.payment.api.config.ApplePayConfig
import com.kttipay.payment.api.config.GooglePayConfig
import com.kttipay.payment.api.config.MobilePaymentConfig
import com.kttipay.payment.api.config.PlatformPaymentConfig

/**
 * Mobile implementation of ConfigAccessor.
 *
 * Provides direct access to mobile configurations.
 */
internal class MobileConfigAccessor : ConfigAccessor {

    override fun getApplePayConfig(config: PlatformPaymentConfig): ApplePayConfig? {
        require(config is MobilePaymentConfig) {
            "MobileConfigAccessor requires MobilePaymentConfig"
        }
        return config.applePayMobile
    }

    override fun getGooglePayConfig(config: PlatformPaymentConfig): GooglePayConfig? {
        return config.googlePay
    }
}
