package com.kttipay.payment.api.config

/**
 * Configuration for Google Pay integration (shared across Android and Web).
 *
 * @param merchantId Your Google Pay merchant ID from the Google Pay & Wallet Console.
 * @param merchantName The name of your business as it should appear to users at checkout.
 * @param gateway Gateway-specific tokenization configuration. See [GatewayConfig].
 * @param allowedCardNetworks Set of card networks to accept. Defaults to MASTERCARD and VISA.
 * @param allowedAuthMethods Set of authentication methods to accept. Defaults to PAN_ONLY + CRYPTOGRAM_3DS.
 * @param allowCreditCards Whether to allow credit card transactions. Defaults to false.
 * @param assuranceDetailsRequired Whether to request additional cardholder verification.
 *                                 Defaults to false. Set to true to request additional verification
 *                                 for enhanced security.
 * @param currencyCode ISO 4217 currency code (e.g. "USD", "EUR", "AUD").
 * @param countryCode ISO 3166-1 alpha-2 country code (e.g. "US", "GB", "AU").
 *
 * Example (Stripe):
 * ```
 * GooglePayConfig(
 *     merchantId = "YOUR_MERCHANT_ID",
 *     merchantName = "Your Store",
 *     gateway = GatewayConfig.Stripe(publishableKey = "pk_live_...")
 * )
 * ```
 *
 * Example (FatZebra / Adyen / any other gateway):
 * ```
 * GooglePayConfig(
 *     merchantId = "YOUR_MERCHANT_ID",
 *     merchantName = "Your Store",
 *     gateway = GatewayConfig.Custom(
 *         gatewayName = "fatzebra",
 *         gatewayMerchantId = "<your merchant id>"
 *     )
 * )
 * ```
 */
data class GooglePayConfig(
    val merchantId: String,
    val merchantName: String,
    val gateway: GatewayConfig,
    val allowedCardNetworks: Set<GooglePayCardNetwork> = setOf(
        GooglePayCardNetwork.MASTERCARD,
        GooglePayCardNetwork.VISA,
    ),
    val allowedAuthMethods: Set<GooglePayAuthMethod> = GooglePayAuthMethod.DEFAULT,
    val allowCreditCards: Boolean = false,
    val assuranceDetailsRequired: Boolean = false,
    val currencyCode: String = "AUD",
    val countryCode: String = "AU",
) {
    init {
        require(merchantId.isNotBlank()) { "merchantId cannot be blank" }
        require(merchantName.isNotBlank()) { "merchantName cannot be blank" }
        require(currencyCode.isNotBlank()) { "currencyCode cannot be blank" }
        require(currencyCode.length == 3) { "currencyCode must be a 3-letter ISO 4217 code" }
        require(countryCode.isNotBlank()) { "countryCode cannot be blank" }
        require(countryCode.length == 2) { "countryCode must be a 2-letter ISO 3166-1 alpha-2 code" }
        require(allowedCardNetworks.isNotEmpty()) { "allowedCardNetworks cannot be empty" }
        require(allowedAuthMethods.isNotEmpty()) { "allowedAuthMethods cannot be empty" }
    }
}
