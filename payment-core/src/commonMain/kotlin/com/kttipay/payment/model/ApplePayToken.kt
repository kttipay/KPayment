package com.kttipay.payment.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplePayToken(
    val paymentData: ApplePaymentData
)

@Serializable
data class ApplePaymentData(
    val data: String,
    val header: ApplePayHeader,
    val signature: String,
    val version: String
)

@Serializable
data class ApplePayHeader(
    val ephemeralPublicKey: String,
    val publicKeyHash: String,
    @SerialName("transactionId")
    val transactionId: String
)

