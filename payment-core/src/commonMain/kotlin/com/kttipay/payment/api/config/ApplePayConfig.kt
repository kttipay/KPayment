package com.kttipay.payment.api.config

interface ApplePayConfig {
    val merchantName: String
    val supportedNetworks: Set<ApplePayNetwork>
    val merchantCapabilities: Set<ApplePayMerchantCapability>
    val currencyCode: String
    val countryCode: String
}