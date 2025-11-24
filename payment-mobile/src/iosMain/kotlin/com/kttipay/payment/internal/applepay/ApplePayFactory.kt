package com.kttipay.payment.internal.applepay

import org.kimplify.deci.Deci

interface ApplePayFactory {
    fun applePayStatus(): ApplePayStatus

    fun startPayment(
        request: ApplePayRequest,
        onResult: (ApplePayResult) -> Unit
    )

    fun presentSetupFlow(onFinished: (Boolean) -> Unit = {})
}


data class SummaryItem(
    val label: String,
    val amount: Deci,
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
    val supportedNetworks: List<String> = listOf("amex", "discover", "masterCard", "visa"),
    val merchantCapabilities3DS: Boolean = true,
    val supportsCouponCode: Boolean = false,
    val initialCouponCode: String? = null
)

data class ApplePayStatus(
    val canMakePayments: Boolean,
    val canSetupCards: Boolean
)

sealed interface ApplePayResult {
    /** Success with the UTF-8 JSON from PKPaymentToken.paymentData. */
    data class Success(
        val tokenJson: String,
        val transactionIdentifier: String?
    ) : ApplePayResult

    data object Cancelled : ApplePayResult

    data class Failure(
        val message: String,
        val errorCode: String? = null
    ) : ApplePayResult
}
