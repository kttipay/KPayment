package com.kttipay.payment.model

import kotlinx.serialization.Serializable

@Serializable
data class GooglePayToken(
    val protocolVersion: String,
    val signature: String,
    val intermediateSigningKey: IntermediateSigningKey,
    val signedMessage: String
)

@Serializable
data class IntermediateSigningKey(
    val signatures: List<String>,
    val signedKey: String
)

@Serializable
data class SignedKey(
    val keyValue: String,
    val keyExpiration: String
)

data class GooglePayTokenPayload(
    val token: GooglePayToken,
    val signedKey: SignedKey
)

