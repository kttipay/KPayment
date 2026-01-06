@file:OptIn(ExperimentalWasmJsInterop::class)

package com.kttipay.payment.internal.googlepay

import com.kttipay.payment.api.config.GooglePayWebConfig
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsBoolean
import kotlin.js.Promise


internal external interface PaymentMethodTokenizationData : JsAny {
    val token: String
}

internal external interface PaymentMethodData : JsAny {
    val tokenizationData: PaymentMethodTokenizationData
}

internal external interface PaymentData : JsAny {
    val paymentMethodData: PaymentMethodData
}

@JsFun("function(googlePayEnvironment) { return new google.payments.api.PaymentsClient({ environment: googlePayEnvironment }) } ")
external fun createPaymentsClient(googlePayEnvironment: String): PaymentsClient

external interface PaymentsClient : JsAny {
    fun isReadyToPay(request: JsAny): Promise<JsBoolean>
    fun loadPaymentData(request: JsAny): Promise<JsAny>
}

internal external interface IsReadyToPayResponse : JsAny {
    val result: Boolean
}

internal fun getIsReadyToPayRequest(config: GooglePayWebConfig): JsAny {
    val cardPaymentMethod = buildCardPaymentMethod(
        allowedAuthMethods = config.allowedAuthMethods.map { it.value },
        allowedCardNetworks = config.allowedCardNetworks.map { it.value },
        assuranceDetailsRequired = config.assuranceDetailsRequired,
        allowCreditCards = config.allowCreditCards
    )
    return buildReadyToPayRequest(cardPaymentMethod)
}

private fun buildPaymentDataRequest(
    totalPrice: String,
    config: GooglePayWebConfig
): JsAny {
    val cardPaymentMethod = buildCardPaymentMethod(
        allowedAuthMethods = config.allowedAuthMethods.map { it.value },
        allowedCardNetworks = config.allowedCardNetworks.map { it.value },
        assuranceDetailsRequired = config.assuranceDetailsRequired,
        allowCreditCards = config.allowCreditCards
    )
    val tokenizationSpec = buildTokenizationSpecification(
        gateway = config.googlePayGateway,
        gatewayMerchantId = config.googlePayGatewayMerchantId
    )
    return buildPaymentDataRequestJs(
        totalPrice = totalPrice,
        currencyCode = config.currencyCode,
        countryCode = config.countryCode,
        merchantId = config.googlePayMerchantId,
        merchantName = config.googlePayMerchantName,
        cardPaymentMethod = cardPaymentMethod,
        tokenizationSpec = tokenizationSpec
    )
}

private fun buildCardPaymentMethod(
    allowedAuthMethods: List<String>,
    allowedCardNetworks: List<String>,
    assuranceDetailsRequired: Boolean,
    allowCreditCards: Boolean
): JsAny {
    val authMethodsJson = allowedAuthMethods.joinToString(",") { "\"$it\"" }
    val cardNetworksJson = allowedCardNetworks.joinToString(",") { "\"$it\"" }
    return buildCardPaymentMethodJs(
        authMethodsJson,
        cardNetworksJson,
        assuranceDetailsRequired,
        allowCreditCards
    )
}

@JsFun(
    """
    function(allowedAuthMethods, allowedCardNetworks, assuranceDetailsRequired, allowCreditCards) {
        return {
            type: 'CARD',
            parameters: {
                allowedAuthMethods: JSON.parse('[' + allowedAuthMethods + ']'),
                allowedCardNetworks: JSON.parse('[' + allowedCardNetworks + ']'),
                assuranceDetailsRequired: assuranceDetailsRequired,
                allowCreditCards: allowCreditCards
            }
        };
    }
"""
)
private external fun buildCardPaymentMethodJs(
    allowedAuthMethods: String,
    allowedCardNetworks: String,
    assuranceDetailsRequired: Boolean,
    allowCreditCards: Boolean
): JsAny

@JsFun(
    """
    function(cardPaymentMethod) {
        return {
            apiVersion: 2,
            apiVersionMinor: 0,
            allowedPaymentMethods: [cardPaymentMethod]
        };
    }
"""
)
private external fun buildReadyToPayRequest(cardPaymentMethod: JsAny): JsAny

@JsFun(
    """
    function(gateway, gatewayMerchantId) {
        return {
            type: 'PAYMENT_GATEWAY',
            parameters: {
                gateway: gateway,
                gatewayMerchantId: gatewayMerchantId
            }
        };
    }
"""
)
private external fun buildTokenizationSpecification(
    gateway: String,
    gatewayMerchantId: String
): JsAny

@JsFun(
    """
    function(totalPrice, currencyCode, countryCode, merchantId, merchantName, cardPaymentMethod, tokenizationSpec) {
        cardPaymentMethod.tokenizationSpecification = tokenizationSpec;
        return {
            apiVersion: 2,
            apiVersionMinor: 0,
            allowedPaymentMethods: [cardPaymentMethod],
            merchantInfo: {
                merchantName: merchantName,
                merchantId: merchantId
            },
            transactionInfo: {
                totalPriceStatus: 'FINAL',
                totalPrice: totalPrice,
                currencyCode: currencyCode,
                countryCode: countryCode
            }
        };
    }
"""
)
private external fun buildPaymentDataRequestJs(
    totalPrice: String,
    currencyCode: String,
    countryCode: String,
    merchantId: String,
    merchantName: String,
    cardPaymentMethod: JsAny,
    tokenizationSpec: JsAny
): JsAny

@OptIn(ExperimentalWasmJsInterop::class)
internal fun loadPaymentDataRequestWithDefaults(
    totalPrice: String,
    config: GooglePayWebConfig
): JsAny {
    return buildPaymentDataRequest(
        totalPrice = totalPrice,
        config = config
    )
}
