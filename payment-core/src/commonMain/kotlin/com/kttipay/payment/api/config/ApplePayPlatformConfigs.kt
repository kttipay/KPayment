package com.kttipay.payment.api.config


/**
 * Mobile specific configuration that augments the shared [ApplePayBaseConfig]
 * with the merchant identifier required by PassKit.
 */
data class ApplePayMobileConfig(
    val base: ApplePayBaseConfig,
    val merchantId: String
) : ApplePayConfig by base

/**
 * Web specific configuration that augments [ApplePayBaseConfig] with the merchant
 * validation endpoint exposed by the backend integration.
 */
data class ApplePayWebConfig(
    val base: ApplePayBaseConfig,
    val merchantValidationEndpoint: String,
    val baseUrl: String,
    val domain: String
) : ApplePayConfig by base
