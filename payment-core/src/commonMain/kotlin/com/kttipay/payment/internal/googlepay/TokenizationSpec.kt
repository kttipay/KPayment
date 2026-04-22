package com.kttipay.payment.internal.googlepay

import com.kttipay.payment.api.config.GatewayConfig

/**
 * Builds the `tokenizationSpecification.parameters` object that Google Pay expects for this
 * gateway. Both Android and Web consume this map — single source of truth for the spec.
 */
internal fun GatewayConfig.toTokenizationParameters(): Map<String, String> = when (this) {
    is GatewayConfig.Stripe -> buildMap {
        put("gateway", "stripe")
        put("stripe:version", apiVersion)
        put("stripe:publishableKey", publishableKey)
        stripeAccountId?.let { put("gatewayMerchantId", it) }
    }
    is GatewayConfig.Custom -> buildMap {
        require(
            !additionalParameters.containsKey("gateway") &&
                !additionalParameters.containsKey("gatewayMerchantId")
        ) {
            "Custom.additionalParameters cannot contain reserved keys 'gateway' or 'gatewayMerchantId' — use the typed fields instead"
        }
        put("gateway", gatewayName)
        gatewayMerchantId?.let { put("gatewayMerchantId", it) }
        putAll(additionalParameters)
    }
}

/**
 * Minimal, allocation-light JSON-object serializer for `Map<String, String>`. Keeps
 * `payment-core` free of a kotlinx.serialization dependency for this single use case.
 * Escapes `"`, `\`, and the JSON-required control characters.
 */
internal fun Map<String, String>.toJsonObjectString(): String {
    val sb = StringBuilder()
    sb.append('{')
    var first = true
    for ((k, v) in this) {
        if (!first) sb.append(',')
        first = false
        sb.append('"').appendEscaped(k).append('"').append(':').append('"').appendEscaped(v).append('"')
    }
    sb.append('}')
    return sb.toString()
}

private fun StringBuilder.appendEscaped(s: String): StringBuilder {
    for (c in s) {
        when (c) {
            '"' -> append("\\\"")
            '\\' -> append("\\\\")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            '\b' -> append("\\b")
            else -> if (c.code < 0x20) {
                append("\\u")
                val hex = c.code.toString(16)
                repeat(4 - hex.length) { append('0') }
                append(hex)
            } else {
                append(c)
            }
        }
    }
    return this
}
