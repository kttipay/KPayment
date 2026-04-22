package com.kttipay.payment.internal.googlepay

import com.kttipay.payment.api.config.GatewayConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TokenizationSpecTest {

    @Test
    fun `Stripe minimal emits gateway + version + publishableKey, no gatewayMerchantId`() {
        val params = GatewayConfig.Stripe(publishableKey = "pk_live_x")
            .toTokenizationParameters()
        assertEquals(
            mapOf(
                "gateway" to "stripe",
                "stripe:version" to "2018-10-31",
                "stripe:publishableKey" to "pk_live_x",
            ),
            params
        )
    }

    @Test
    fun `Stripe with stripeAccountId emits gatewayMerchantId`() {
        val params = GatewayConfig.Stripe(
            publishableKey = "pk_live_x",
            stripeAccountId = "acct_1"
        ).toTokenizationParameters()
        assertEquals("acct_1", params["gatewayMerchantId"])
        assertEquals("pk_live_x", params["stripe:publishableKey"])
    }

    @Test
    fun `Stripe honours custom apiVersion`() {
        val params = GatewayConfig.Stripe(
            publishableKey = "pk_x",
            apiVersion = "2019-12-01"
        ).toTokenizationParameters()
        assertEquals("2019-12-01", params["stripe:version"])
    }

    @Test
    fun `Custom emits gateway + gatewayMerchantId (FatZebra shape)`() {
        val params = GatewayConfig.Custom(
            gatewayName = "fatzebra",
            gatewayMerchantId = "my_fz_merchant"
        ).toTokenizationParameters()
        assertEquals(
            mapOf(
                "gateway" to "fatzebra",
                "gatewayMerchantId" to "my_fz_merchant",
            ),
            params
        )
    }

    @Test
    fun `Custom emits gateway + additionalParameters (Braintree shape)`() {
        val params = GatewayConfig.Custom(
            gatewayName = "braintree",
            additionalParameters = mapOf(
                "braintree:apiVersion" to "v1",
                "braintree:clientKey" to "ck_prod",
            )
        ).toTokenizationParameters()
        assertEquals("braintree", params["gateway"])
        assertEquals("v1", params["braintree:apiVersion"])
        assertEquals("ck_prod", params["braintree:clientKey"])
        assertTrue(!params.containsKey("gatewayMerchantId"))
    }

    @Test
    fun `Custom merges gatewayMerchantId and additionalParameters`() {
        val params = GatewayConfig.Custom(
            gatewayName = "adyen",
            gatewayMerchantId = "YourAdyenAccount",
            additionalParameters = mapOf("adyen:extra" to "x")
        ).toTokenizationParameters()
        assertEquals(3, params.size)
        assertEquals("YourAdyenAccount", params["gatewayMerchantId"])
        assertEquals("x", params["adyen:extra"])
    }

    @Test
    fun `Custom with reserved gateway key in additionalParameters throws`() {
        assertFailsWith<IllegalArgumentException> {
            GatewayConfig.Custom(
                gatewayName = "braintree",
                additionalParameters = mapOf("gateway" to "x")
            ).toTokenizationParameters()
        }
    }

    @Test
    fun `Custom with reserved gatewayMerchantId key in additionalParameters throws`() {
        assertFailsWith<IllegalArgumentException> {
            GatewayConfig.Custom(
                gatewayName = "braintree",
                additionalParameters = mapOf("gatewayMerchantId" to "x")
            ).toTokenizationParameters()
        }
    }

    @Test
    fun `toJsonObjectString emits valid JSON with no escaping needed`() {
        val json = mapOf("a" to "b", "c" to "d").toJsonObjectString()
        // Order is not guaranteed on all platforms, but both keys must be present
        assertTrue(json.startsWith("{") && json.endsWith("}"))
        assertTrue(json.contains("\"a\":\"b\""))
        assertTrue(json.contains("\"c\":\"d\""))
    }

    @Test
    fun `toJsonObjectString escapes double quotes and backslashes`() {
        val json = mapOf("k" to "a\"b\\c").toJsonObjectString()
        assertEquals("""{"k":"a\"b\\c"}""", json)
    }

    @Test
    fun `toJsonObjectString escapes control characters`() {
        val json = mapOf("k" to "a\nb\tc").toJsonObjectString()
        assertEquals("""{"k":"a\nb\tc"}""", json)
    }

    @Test
    fun `toJsonObjectString emits empty object for empty map`() {
        assertEquals("{}", emptyMap<String, String>().toJsonObjectString())
    }
}
