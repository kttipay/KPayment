package com.kttipay.payment.api.config

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
)
