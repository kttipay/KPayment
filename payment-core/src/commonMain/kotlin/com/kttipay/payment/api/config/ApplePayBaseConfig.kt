package com.kttipay.payment.api.config

/**
 * Platform-agnostic Apple Pay configuration shared by every surface.
 */
data class ApplePayBaseConfig(
    override val merchantName: String,
    override val supportedNetworks: List<String> = listOf("visa", "masterCard", "amex"),
    override val merchantCapabilities: List<String> = listOf("supports3DS", "supportsDebit"),
    override val currencyCode: String = "AUD",
    override val countryCode: String = "AU"
) : ApplePayConfig
