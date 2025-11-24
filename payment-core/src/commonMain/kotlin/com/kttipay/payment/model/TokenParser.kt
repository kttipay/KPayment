package com.kttipay.payment.model

import kotlinx.serialization.json.Json
import org.kimplify.cedar.logging.Cedar

private val json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

object ApplePayTokenParser {
    private val supportedVersions = setOf("EC_v1", "RSA_v1")

    fun parse(jsonString: String): Result<ApplePaymentData> = runCatching {
        Cedar.tag("ApplePayTokenParser").d("Parsing Apple Pay token")
        val token = json.decodeFromString<ApplePayToken>(jsonString)
        require(token.paymentData.version in supportedVersions) {
            "Unsupported Apple Pay token version: ${token.paymentData.version}"
        }
        token.paymentData
    }.onFailure {
        Cedar.tag("ApplePayTokenParser").w("Error parsing Apple Pay token", it)
    }
}

object GooglePayTokenParser {
    private const val SUPPORTED_PROTOCOL_VERSION = "ECv2"

    fun parse(jsonString: String): Result<GooglePayTokenPayload> = runCatching {
        Cedar.tag("GooglePayTokenParser").d("Parsing Google Pay token")
        val token = json.decodeFromString<GooglePayToken>(jsonString)
        require(token.protocolVersion == SUPPORTED_PROTOCOL_VERSION) {
            "Unsupported protocol version: ${token.protocolVersion}"
        }
        val signedKey = json.decodeFromString<SignedKey>(token.intermediateSigningKey.signedKey)
        GooglePayTokenPayload(token = token, signedKey = signedKey)
    }.onFailure {
        Cedar.tag("GooglePayTokenParser").w("Error parsing Google Pay token", it)
    }
}

