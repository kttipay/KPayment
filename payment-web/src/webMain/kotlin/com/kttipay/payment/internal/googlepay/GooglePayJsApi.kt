@file:OptIn(ExperimentalWasmJsInterop::class)

package com.kttipay.payment.internal.googlepay

import com.kttipay.payment.api.config.GooglePayWebConfig
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsBoolean
import kotlin.js.Promise
import kotlin.js.js


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


internal fun getIsReadyToPayRequest(): JsAny = js(
    """
({
  apiVersion: 2,
  apiVersionMinor: 0,
  allowedPaymentMethods: [{
    type: 'CARD',
    parameters: {
      allowedAuthMethods: ['PAN_ONLY','CRYPTOGRAM_3DS'],
      allowedCardNetworks: ['MASTERCARD','VISA'],
      assuranceDetailsRequired: true,
      allowCreditCards: false
    }
  }]
})
"""
)


@OptIn(ExperimentalWasmJsInterop::class)
@JsFun(
    """
    function(totalPrice, currencyCode, countryCode, gateway, gatewayMerchantId, merchantId, merchantName) {
        return {
            apiVersion: 2,
            apiVersionMinor: 0,
            allowedPaymentMethods: [{
                type: 'CARD',
                parameters: {
                    allowedAuthMethods: ['PAN_ONLY', 'CRYPTOGRAM_3DS'],
                    allowedCardNetworks: ['MASTERCARD', 'VISA'],
                    assuranceDetailsRequired: true,
                    allowCreditCards: false
                },
                tokenizationSpecification: {
                    type: 'PAYMENT_GATEWAY',
                    parameters: {
                        gateway: gateway,
                        gatewayMerchantId: gatewayMerchantId
                    }
                }
            }],
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
external fun buildPaymentDataRequest(
    totalPrice: String,
    currencyCode: String,
    countryCode: String,
    gateway: String,
    gatewayMerchantId: String,
    merchantId: String,
    merchantName: String
): JsAny

@OptIn(ExperimentalWasmJsInterop::class)
internal fun loadPaymentDataRequestWithDefaults(
    totalPrice: String,
    config: GooglePayWebConfig
): JsAny {
    return buildPaymentDataRequest(
        totalPrice = totalPrice,
        currencyCode = config.currencyCode,
        countryCode = config.countryCode,
        gateway = config.googlePayGateway,
        gatewayMerchantId = config.googlePayGatewayMerchantId,
        merchantId = config.googlePayMerchantId,
        merchantName = config.googlePayMerchantName
    )
}
