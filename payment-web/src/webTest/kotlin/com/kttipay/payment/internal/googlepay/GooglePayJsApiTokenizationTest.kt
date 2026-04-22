@file:OptIn(ExperimentalWasmJsInterop::class)

package com.kttipay.payment.internal.googlepay

import com.kttipay.payment.api.config.GatewayConfig
import com.kttipay.payment.api.config.GooglePayAuthMethod
import com.kttipay.payment.api.config.GooglePayCardNetwork
import com.kttipay.payment.api.config.GooglePayWebConfig
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Lightweight web regression test: serializes the JsAny payment data request to a JSON string
 * via `JSON.stringify`, re-parses, and asserts the tokenizationSpecification.parameters shape.
 * Direct analog of the Android test in payment-mobile.
 */
class GooglePayJsApiTokenizationTest {

    private fun config(gateway: GatewayConfig) = GooglePayWebConfig(
        googlePayEnvironment = "TEST",
        gateway = gateway,
        googlePayMerchantId = "m_1",
        googlePayMerchantName = "Test Merchant",
        allowedCardNetworks = setOf(GooglePayCardNetwork.VISA, GooglePayCardNetwork.MASTERCARD),
        allowedAuthMethods = setOf(GooglePayAuthMethod.PAN_ONLY),
        allowCreditCards = true,
        currencyCode = "AUD",
        countryCode = "AU",
    )

    private fun parameters(gateway: GatewayConfig): Map<String, String> {
        val request = loadPaymentDataRequestWithDefaults(totalPrice = "10.00", config = config(gateway))
        val json = jsonStringify(request)
        // Extract the tokenizationSpecification.parameters object from the JSON string.
        val marker = "\"tokenizationSpecification\":"
        val start = json.indexOf(marker)
        check(start >= 0) { "tokenizationSpecification missing in serialized request: $json" }
        val paramsKey = "\"parameters\":{"
        val paramsStart = json.indexOf(paramsKey, start)
        check(paramsStart >= 0) { "parameters missing: $json" }
        val open = paramsStart + paramsKey.length - 1 // index of '{'
        var depth = 0
        var end = open
        for (i in open until json.length) {
            when (json[i]) {
                '{' -> depth++
                '}' -> {
                    depth--
                    if (depth == 0) {
                        end = i
                        break
                    }
                }
            }
        }
        val paramsObject = json.substring(open, end + 1) // e.g. {"gateway":"stripe",...}
        return parseFlatJsonObject(paramsObject)
    }

    private fun parseFlatJsonObject(s: String): Map<String, String> {
        // Minimal parser for a one-level object of string-to-string pairs. Sufficient because
        // toTokenizationParameters() guarantees Map<String, String>.
        require(s.startsWith("{") && s.endsWith("}"))
        val body = s.substring(1, s.length - 1).trim()
        if (body.isEmpty()) return emptyMap()
        val out = linkedMapOf<String, String>()
        var i = 0
        while (i < body.length) {
            // Skip whitespace and commas.
            while (i < body.length && (body[i] == ',' || body[i].isWhitespace())) i++
            if (i >= body.length) break
            // Key
            require(body[i] == '"') { "expected key at $i: $body" }
            i++
            val ks = i
            while (body[i] != '"') i++
            val key = body.substring(ks, i)
            i++ // past closing "
            // Colon
            while (body[i].isWhitespace() || body[i] == ':') i++
            // Value
            require(body[i] == '"') { "only string values supported at $i: $body" }
            i++
            val vs = i
            while (body[i] != '"') i++
            val value = body.substring(vs, i)
            i++
            out[key] = value
        }
        return out
    }

    @Test
    fun `Stripe config emits stripe-shaped parameters`() {
        val params = parameters(GatewayConfig.Stripe(publishableKey = "pk_live_x"))
        assertEquals("stripe", params["gateway"])
        assertEquals("2018-10-31", params["stripe:version"])
        assertEquals("pk_live_x", params["stripe:publishableKey"])
        assertTrue(!params.containsKey("gatewayMerchantId"))
    }

    @Test
    fun `FatZebra-shaped Custom emits gateway + gatewayMerchantId`() {
        val params = parameters(
            GatewayConfig.Custom(
                gatewayName = "fatzebra",
                gatewayMerchantId = "fz_merchant_xyz",
            ),
        )
        assertEquals(
            mapOf("gateway" to "fatzebra", "gatewayMerchantId" to "fz_merchant_xyz"),
            params,
        )
    }

    @Test
    fun `type is PAYMENT_GATEWAY`() {
        val json = jsonStringify(
            loadPaymentDataRequestWithDefaults(
                totalPrice = "10.00",
                config = config(GatewayConfig.Stripe(publishableKey = "pk_x")),
            ),
        )
        assertTrue(json.contains("\"type\":\"PAYMENT_GATEWAY\""))
    }
}

@JsFun("function(x) { return JSON.stringify(x); }")
private external fun jsonStringify(x: JsAny): String
