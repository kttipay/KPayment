package com.kttipay.payment.api.config

interface ApplePayConfig {
    val merchantName: String
    val supportedNetworks: List<String>
    val merchantCapabilities: List<String>
    val currencyCode: String
    val countryCode: String
}