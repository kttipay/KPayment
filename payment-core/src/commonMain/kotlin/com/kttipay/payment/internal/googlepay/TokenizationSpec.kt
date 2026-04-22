package com.kttipay.payment.internal.googlepay

import com.kttipay.payment.api.config.GatewayConfig

/**
 * Builds the `tokenizationSpecification.parameters` object that Google Pay expects for this
 * gateway. Both Android and Web consume this map — single source of truth for the spec.
 */
fun GatewayConfig.toTokenizationParameters(): Map<String, String> = when (this) {
    is GatewayConfig.Stripe -> buildMap {
        put("gateway", "stripe")
        put("stripe:version", apiVersion)
        put("stripe:publishableKey", publishableKey)
        stripeAccountId?.let { put("gatewayMerchantId", it) }
    }
    is GatewayConfig.Custom -> buildMap {
        put("gateway", gatewayName)
        gatewayMerchantId?.let { put("gatewayMerchantId", it) }
        putAll(additionalParameters)
    }
}

/**
 * Minimal, allocation-light JSON-object serializer for `Map<String, String>`. Keeps
 * `payment-core` free of a kotlinx.serialization dependency for this single use case.
 * Escapes `"`, `\`, and the JSON-required control characters.
 *
 * Caveat: only C0 controls (U+0000–U+001F) are escaped. C1 controls (U+0080–U+009F) and
 * surrogate pairs are passed through verbatim. This is acceptable here because the only
 * callers are [GatewayConfig.toTokenizationParameters], whose values are gateway names,
 * merchant IDs, and similar ASCII tokens.
 */
fun Map<String, String>.toJsonObjectString(): String {
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
            '' -> append("\\f")
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
