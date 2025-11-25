package com.kttipay.payment.api.config

import com.kttipay.payment.api.PaymentEnvironment

data class GooglePayWebConfig(
    val googlePayEnvironment: String,
    val googlePayGateway: String,
    val googlePayGatewayMerchantId: String,
    val googlePayMerchantId: String,
    val googlePayMerchantName: String,
    val allowedCardNetworks: Set<GooglePayCardNetwork>,
    val allowedAuthMethods: Set<GooglePayAuthMethod>,
    val allowCreditCards: Boolean,
    val currencyCode: String,
    val countryCode: String
)

fun GooglePayConfig.toGooglePayWebConfig(environment: PaymentEnvironment): GooglePayWebConfig {
    return GooglePayWebConfig(
        googlePayEnvironment = when (environment) {
            PaymentEnvironment.Production -> "PRODUCTION"
            PaymentEnvironment.Development -> "TEST"
        },
        googlePayGateway = gateway,
        googlePayGatewayMerchantId = gatewayMerchantId,
        googlePayMerchantId = merchantId,
        googlePayMerchantName = merchantName,
        allowedCardNetworks = allowedCardNetworks,
        allowedAuthMethods = allowedAuthMethods,
        allowCreditCards = allowCreditCards,
        currencyCode = currencyCode,
        countryCode = countryCode
    )
}
