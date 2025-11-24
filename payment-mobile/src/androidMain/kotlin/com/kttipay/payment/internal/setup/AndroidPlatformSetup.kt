package com.kttipay.payment.internal.setup

import com.kttipay.payment.api.config.MobilePaymentConfig
import com.kttipay.payment.internal.googlepay.GooglePayService

/**
 * Android implementation of PlatformSetup.
 *
 * Configures Google Pay service when Google Pay configuration is present.
 *
 * @param googlePayService Service to configure for Google Pay operations
 */
internal class AndroidPlatformSetup(
    private val googlePayService: GooglePayService
) : PlatformSetup {

    override fun setupPlatformPayments(config: MobilePaymentConfig) {
        config.googlePay?.let {
            googlePayService.configure(it, config.environment)
        }
    }
}
