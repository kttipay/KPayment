package com.kttipay.payment.api.config

/**
 * Configuration for Google Pay integration.
 *
 * This configuration is used across Android and Web platforms to set up Google Pay.
 * All required parameters must be provided, while optional parameters have sensible defaults.
 *
 * @param merchantId Your Google Pay merchant ID. This is the merchant identifier registered
 *                  with Google Pay Business Console. Must not be empty.
 * @param merchantName The name of your business as it should appear to users during payment.
 *                     Must not be empty.
 * @param gateway The payment gateway identifier (e.g., "stripe", "braintree", "adyen").
 *                Must not be empty.
 * @param gatewayMerchantId Your merchant identifier with the payment gateway.
 *                          This is used by the gateway to identify your account.
 *                          Must not be empty.
 * @param allowedCardNetworks Set of card networks to accept. Defaults to MASTERCARD and VISA.
 *                           Use [GooglePayCardNetwork] enum values.
 * @param allowedAuthMethods Set of authentication methods to accept.
 *                          Defaults to [GooglePayAuthMethod.DEFAULT] (PAN_ONLY and CRYPTOGRAM_3DS).
 * @param allowCreditCards Whether to allow credit card transactions. Defaults to false.
 *                        Set to true if you want to accept credit cards in addition to debit cards.
 * @param assuranceDetailsRequired Whether to require additional cardholder verification.
 *                                 Defaults to false. Set to true to request additional verification
 *                                 for enhanced security.
 * @param currencyCode ISO 4217 currency code (e.g., "USD", "EUR", "AUD")
 * @param countryCode ISO 3166-1 alpha-2 country code (e.g., "US", "GB", "AU")
 *
 * Example usage:
 * ```
 * val googlePayConfig = GooglePayConfig(
 *     merchantId = "YOUR_MERCHANT_ID",
 *     merchantName = "Your Store",
 *     gateway = "stripe",
 *     gatewayMerchantId = "YOUR_GATEWAY_ID",
 *     currencyCode = "USD",
 *     countryCode = "US"
 * )
 * ```
 */
data class GooglePayConfig(
    val merchantId: String,
    val merchantName: String,
    val gateway: String,
    val gatewayMerchantId: String,
    val allowedCardNetworks: Set<GooglePayCardNetwork> = setOf(
        GooglePayCardNetwork.MASTERCARD,
        GooglePayCardNetwork.VISA
    ),
    val allowedAuthMethods: Set<GooglePayAuthMethod> = GooglePayAuthMethod.DEFAULT,
    val allowCreditCards: Boolean = false,
    val assuranceDetailsRequired: Boolean = false,
    val currencyCode: String = "AUD",
    val countryCode: String = "AU"
) {
    init {
        require(merchantId.isNotBlank()) { "merchantId cannot be blank" }
        require(merchantName.isNotBlank()) { "merchantName cannot be blank" }
        require(gateway.isNotBlank()) { "gateway cannot be blank" }
        require(gatewayMerchantId.isNotBlank()) { "gatewayMerchantId cannot be blank" }
        require(currencyCode.isNotBlank()) { "currencyCode cannot be blank" }
        require(currencyCode.length == 3) { "currencyCode must be a 3-letter ISO 4217 code" }
        require(countryCode.isNotBlank()) { "countryCode cannot be blank" }
        require(countryCode.length == 2) { "countryCode must be a 2-letter ISO 3166-1 alpha-2 code" }
        require(allowedCardNetworks.isNotEmpty()) { "allowedCardNetworks cannot be empty" }
        require(allowedAuthMethods.isNotEmpty()) { "allowedAuthMethods cannot be empty" }
    }
}
