package com.kttipay.payment.api.config

/**
 * Platform-agnostic Apple Pay configuration shared by every surface.
 *
 * This configuration is used as the base for both mobile (iOS) and web (Safari) Apple Pay setups.
 * It contains the common configuration options that apply across all platforms.
 *
 * @param merchantName The name of your business as it should appear to users during payment.
 *                     Must not be empty.
 * @param supportedNetworks Set of card networks to accept. Defaults to VISA, MASTERCARD, and AMEX.
 *                         Use [ApplePayNetwork] enum values.
 * @param merchantCapabilities Set of merchant capabilities your business supports.
 *                            Defaults to [ApplePayMerchantCapability.DEFAULT] (CAPABILITY_3DS and CAPABILITY_DEBIT).
 *                            Use [ApplePayMerchantCapability] enum values.
 * @param currencyCode ISO 4217 currency code (e.g., "USD", "EUR", "AUD"). Defaults to "AUD".
 * @param countryCode ISO 3166-1 alpha-2 country code (e.g., "US", "GB", "AU"). Defaults to "AU".
 *
 * Example usage:
 * ```
 * val appleBaseConfig = ApplePayBaseConfig(
 *     merchantName = "Your Store",
 *     supportedNetworks = setOf(
 *         ApplePayNetwork.VISA,
 *         ApplePayNetwork.MASTERCARD
 *     ),
 *     currencyCode = "USD",
 *     countryCode = "US"
 * )
 * ```
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
) : ApplePayConfig {
    init {
        require(merchantName.isNotBlank()) { "merchantName cannot be blank" }
        require(supportedNetworks.isNotEmpty()) { "supportedNetworks cannot be empty" }
        require(merchantCapabilities.isNotEmpty()) { "merchantCapabilities cannot be empty" }
        require(currencyCode.isNotBlank()) { "currencyCode cannot be blank" }
        require(currencyCode.length == 3) { "currencyCode must be a 3-letter ISO 4217 code" }
        require(countryCode.isNotBlank()) { "countryCode cannot be blank" }
        require(countryCode.length == 2) { "countryCode must be a 2-letter ISO 3166-1 alpha-2 code" }
    }
}
