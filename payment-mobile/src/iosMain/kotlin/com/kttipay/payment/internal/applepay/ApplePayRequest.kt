package com.kttipay.payment.internal.applepay

import com.kttipay.payment.api.config.ApplePayMerchantCapability
import com.kttipay.payment.api.config.ApplePayNetwork

data class SummaryItem(
    val label: String,
    val amount: String,
    /** Maps to PKPaymentSummaryItemType.final or .pending */
    val isFinal: Boolean = true
)

data class ApplePayRequest(
    /** PKPaymentRequest.merchantIdentifier */
    val merchantId: String,
    /** iso2 country code */
    val countryCode: String,
    val currencyCode: String,
    val summaryItems: List<SummaryItem>,
    val supportedNetworks: Set<ApplePayNetwork> = setOf(
        ApplePayNetwork.AMEX,
        ApplePayNetwork.DISCOVER,
        ApplePayNetwork.MASTERCARD,
        ApplePayNetwork.VISA
    ),
    val merchantCapabilities: Set<ApplePayMerchantCapability> = ApplePayMerchantCapability.DEFAULT,
    val supportsCouponCode: Boolean = false,
    val initialCouponCode: String? = null
)
