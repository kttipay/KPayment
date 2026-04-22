package com.kttipay.payment.api.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GatewayConfigTest {

    // ---- Stripe ----

    @Test
    fun `Stripe constructs with required publishableKey`() {
        val config = GatewayConfig.Stripe(publishableKey = "pk_live_x")
        assertEquals("pk_live_x", config.publishableKey)
        assertEquals("2018-10-31", config.apiVersion)
        assertEquals(null, config.stripeAccountId)
    }

    @Test
    fun `Stripe accepts custom apiVersion and stripeAccountId`() {
        val config = GatewayConfig.Stripe(
            publishableKey = "pk_test_x",
            apiVersion = "2019-12-01",
            stripeAccountId = "acct_1"
        )
        assertEquals("2019-12-01", config.apiVersion)
        assertEquals("acct_1", config.stripeAccountId)
    }

    @Test
    fun `Stripe blank publishableKey throws`() {
        assertFailsWith<IllegalArgumentException> {
            GatewayConfig.Stripe(publishableKey = "")
        }
    }

    @Test
    fun `Stripe blank apiVersion throws`() {
        assertFailsWith<IllegalArgumentException> {
            GatewayConfig.Stripe(publishableKey = "pk_x", apiVersion = "")
        }
    }

    @Test
    fun `Stripe blank stripeAccountId (non-null) throws`() {
        assertFailsWith<IllegalArgumentException> {
            GatewayConfig.Stripe(publishableKey = "pk_x", stripeAccountId = "")
        }
    }

    // ---- Custom ----

    @Test
    fun `Custom accepts gatewayMerchantId only`() {
        val config = GatewayConfig.Custom(
            gatewayName = "fatzebra",
            gatewayMerchantId = "merchant_id_123"
        )
        assertEquals("fatzebra", config.gatewayName)
        assertEquals("merchant_id_123", config.gatewayMerchantId)
        assertEquals(emptyMap(), config.additionalParameters)
    }

    @Test
    fun `Custom accepts additionalParameters only`() {
        val config = GatewayConfig.Custom(
            gatewayName = "braintree",
            additionalParameters = mapOf("braintree:clientKey" to "ck_x")
        )
        assertEquals(null, config.gatewayMerchantId)
        assertEquals(mapOf("braintree:clientKey" to "ck_x"), config.additionalParameters)
    }

    @Test
    fun `Custom accepts both gatewayMerchantId and additionalParameters`() {
        val config = GatewayConfig.Custom(
            gatewayName = "adyen",
            gatewayMerchantId = "YourAdyenAccount",
            additionalParameters = mapOf("adyen:key" to "v")
        )
        assertEquals("YourAdyenAccount", config.gatewayMerchantId)
        assertEquals(1, config.additionalParameters.size)
    }

    @Test
    fun `Custom blank gatewayName throws`() {
        assertFailsWith<IllegalArgumentException> {
            GatewayConfig.Custom(gatewayName = "", gatewayMerchantId = "x")
        }
    }

    @Test
    fun `Custom with neither gatewayMerchantId nor additionalParameters throws`() {
        assertFailsWith<IllegalArgumentException> {
            GatewayConfig.Custom(gatewayName = "fatzebra")
        }
    }

    @Test
    fun `Custom blank gatewayMerchantId (non-null) throws`() {
        assertFailsWith<IllegalArgumentException> {
            GatewayConfig.Custom(gatewayName = "fatzebra", gatewayMerchantId = "")
        }
    }

    @Test
    fun `Custom blank additionalParameters key throws`() {
        assertFailsWith<IllegalArgumentException> {
            GatewayConfig.Custom(
                gatewayName = "x",
                additionalParameters = mapOf("" to "v")
            )
        }
    }

    @Test
    fun `Custom blank additionalParameters value throws`() {
        assertFailsWith<IllegalArgumentException> {
            GatewayConfig.Custom(
                gatewayName = "x",
                additionalParameters = mapOf("k" to "")
            )
        }
    }
}
