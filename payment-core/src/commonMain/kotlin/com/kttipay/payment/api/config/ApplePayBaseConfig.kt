package com.kttipay.payment.api.config

/**
 * Platform-agnostic Apple Pay configuration shared by every surface.
 */
data class ApplePayBaseConfig(
    override val merchantName: String,
    override val supportedNetworks: Set<ApplePayNetwork> = setOf(
        ApplePayNetwork.VISA,
        ApplePayNetwork.MASTERCARD,
        ApplePayNetwork.AMEX
    ),
    override val merchantCapabilities: Set<ApplePayMerchantCapability> = ApplePayMerchantCapability.DEFAULT,
    override val currencyCode: String = "AUD",
    override val countryCode: String = "AU"
) : ApplePayConfig
