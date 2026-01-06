package com.kttipay.payment.internal.capability

import com.kttipay.payment.api.config.ApplePayWebConfig
import com.kttipay.payment.api.config.GooglePayWebConfig
import com.kttipay.payment.capability.CapabilityStatus

internal actual fun checkApplePayAvailability(config: ApplePayWebConfig): CapabilityStatus {
    return CapabilityStatus.NotSupported
}

internal actual suspend fun checkGooglePayAvailability(config: GooglePayWebConfig): CapabilityStatus {
    return CapabilityStatus.NotSupported
}
