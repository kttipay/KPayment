package com.kttipay.payment.api.config

data class GooglePayConfig(
    val merchantId: String,
    val merchantName: String,
    val gateway: String,
    val gatewayMerchantId: String,
    val allowedCardNetworks: List<String> = listOf("MASTERCARD", "VISA"),
    val allowedAuthMethods: List<String> = listOf("PAN_ONLY", "CRYPTOGRAM_3DS"),
    val allowCreditCards: Boolean = false,
    val assuranceDetailsRequired: Boolean = false,
    val currencyCode: String = "AUD",
    val countryCode: String = "AU"
)
