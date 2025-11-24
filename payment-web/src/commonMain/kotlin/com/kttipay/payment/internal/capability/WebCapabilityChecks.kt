package com.kttipay.payment.internal.capability

import com.kttipay.payment.api.config.ApplePayWebConfig
import com.kttipay.payment.api.config.GooglePayWebConfig
import com.kttipay.payment.capability.CapabilityStatus

internal expect fun checkApplePayAvailability(
    config: ApplePayWebConfig
): CapabilityStatus

internal expect suspend fun checkGooglePayAvailability(
    config: GooglePayWebConfig
): CapabilityStatus
