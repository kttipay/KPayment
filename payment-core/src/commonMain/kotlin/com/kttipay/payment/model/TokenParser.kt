package com.kttipay.payment.model

import com.kttipay.payment.internal.logging.KPaymentLogger
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

private object ApplePayTokenParser {
    private val supportedVersions = setOf("EC_v1", "RSA_v1")

    fun parse(jsonString: String): Result<ApplePaymentData> = runCatching {
        KPaymentLogger.tag("ApplePayTokenParser").d("Parsing Apple Pay token")
        val token = json.decodeFromString<ApplePayToken>(jsonString)
        require(token.paymentData.version in supportedVersions) {
            "Unsupported Apple Pay token version: ${token.paymentData.version}"
        }
        token.paymentData
    }.onFailure {
        KPaymentLogger.tag("ApplePayTokenParser").w("Error parsing Apple Pay token", it)
    }
}

private object GooglePayTokenParser {
    private const val SUPPORTED_PROTOCOL_VERSION = "ECv2"

    fun parse(jsonString: String): Result<GooglePayTokenPayload> = runCatching {
        KPaymentLogger.tag("GooglePayTokenParser").d("Parsing Google Pay token")
        val token = json.decodeFromString<GooglePayToken>(jsonString)
        require(token.protocolVersion == SUPPORTED_PROTOCOL_VERSION) {
            "Unsupported protocol version: ${token.protocolVersion}"
        }
        val signedKey = json.decodeFromString<SignedKey>(token.intermediateSigningKey.signedKey)
        GooglePayTokenPayload(token = token, signedKey = signedKey)
    }.onFailure {
        KPaymentLogger.tag("GooglePayTokenParser").w("Error parsing Google Pay token", it)
    }
}

