package com.kttipay.payment.capability

import com.kttipay.payment.api.PaymentProvider

sealed interface CapabilityStatus {
    data object NotConfigured : CapabilityStatus
    data object Checking : CapabilityStatus
    data object NotSupported : CapabilityStatus
    data object Ready : CapabilityStatus
    data class Error(val reason: String, val throwable: Throwable? = null) : CapabilityStatus

    val isReady: Boolean
        get() = this == Ready
}

data class PaymentCapabilities(
    val googlePay: CapabilityStatus = CapabilityStatus.NotConfigured,
    val applePay: CapabilityStatus = CapabilityStatus.NotConfigured
) {
    fun statusFor(provider: PaymentProvider): CapabilityStatus = when (provider) {
        PaymentProvider.GooglePay -> googlePay
        PaymentProvider.ApplePay -> applePay
    }

    fun canPayWith(provider: PaymentProvider): Boolean {
        return statusFor(provider) == CapabilityStatus.Ready
    }
}
