package com.kttipay.payment.api.config

/**
 * Mobile-specific Apple Pay configuration for iOS.
 *
 * This configuration augments [ApplePayBaseConfig] with the merchant identifier
 * required by PassKit on iOS devices.
 *
 * @param base The base Apple Pay configuration shared across platforms.
 * @param merchantId Your Apple merchant identifier. This must match the merchant ID
 *                   configured in your Apple Developer account. Format: "merchant.com.yourcompany.app".
 *                   Must not be empty.
 *
 * Example usage:
 * ```
 * val applePayMobile = ApplePayMobileConfig(
 *     merchantId = "merchant.com.yourcompany.app",
 *     base = ApplePayBaseConfig(merchantName = "Your Store")
 * )
 * ```
 */
data class ApplePayMobileConfig(
    val base: ApplePayBaseConfig,
    val merchantId: String
) : ApplePayConfig by base {
    init {
        require(merchantId.isNotBlank()) { "merchantId cannot be blank" }
        require(merchantId.startsWith("merchant.")) { "merchantId must start with 'merchant.'" }
    }
}

/**
 * Web-specific Apple Pay configuration for Safari.
 *
 * This configuration augments [ApplePayBaseConfig] with web-specific settings required
 * for Apple Pay on the Web, including the merchant validation endpoint.
 *
 * Note: Apple Pay on Web only works in Safari and requires domain validation.
 *
 * @param base The base Apple Pay configuration shared across platforms.
 * @param merchantValidationEndpoint The URL endpoint on your backend that handles
 *                                   merchant validation. This endpoint must be accessible
 *                                   via HTTPS and must return the merchant session.
 *                                   Format: "https://example.com/apple-pay/validate".
 *                                   Must not be empty.
 * @param baseUrl The base URL of your website. Used for domain validation.
 *                Format: "https://example.com". Must not be empty.
 * @param domain The domain name for Apple Pay domain verification.
 *               This must match the domain registered in your Apple Developer account.
 *               Format: "example.com" (without protocol). Must not be empty.
 *
 * Example usage:
 * ```
 * val applePayWeb = ApplePayWebConfig(
 *     base = ApplePayBaseConfig(merchantName = "Your Store"),
 *     merchantValidationEndpoint = "https://example.com/apple-pay/validate",
 *     baseUrl = "https://example.com",
 *     domain = "example.com"
 * )
 * ```
 */
data class ApplePayWebConfig(
    val base: ApplePayBaseConfig,
    val merchantValidationEndpoint: String,
    val baseUrl: String,
    val domain: String
) : ApplePayConfig by base {
    init {
        require(merchantValidationEndpoint.isNotBlank()) { "merchantValidationEndpoint cannot be blank" }
        require(merchantValidationEndpoint.startsWith("https://")) { "merchantValidationEndpoint must use HTTPS" }
        require(baseUrl.isNotBlank()) { "baseUrl cannot be blank" }
        require(baseUrl.startsWith("https://")) { "baseUrl must use HTTPS" }
        require(domain.isNotBlank()) { "domain cannot be blank" }
        require(!domain.contains("://")) { "domain must not include protocol (e.g., 'example.com', not 'https://example.com')" }
    }
}
