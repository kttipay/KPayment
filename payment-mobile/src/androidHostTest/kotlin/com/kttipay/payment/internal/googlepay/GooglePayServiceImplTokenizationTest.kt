package com.kttipay.payment.internal.googlepay

import com.kttipay.payment.api.PaymentEnvironment
import com.kttipay.payment.api.config.GatewayConfig
import com.kttipay.payment.api.config.GooglePayConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GooglePayServiceImplTokenizationTest {

    private fun buildService(gateway: GatewayConfig): GooglePayServiceImpl {
        val service = GooglePayServiceImpl()
        service.configure(
            config = GooglePayConfig(
                merchantId = "m_1",
                merchantName = "Test Merchant",
                gateway = gateway,
            ),
            environment = PaymentEnvironment.Development,
        )
        return service
    }

    private fun tokenizationParameters(gateway: GatewayConfig): Map<String, String> {
        val request = buildService(gateway).paymentDataRequest("10.00")
        val method = request.getJSONArray("allowedPaymentMethods").getJSONObject(0)
        val params = method.getJSONObject("tokenizationSpecification").getJSONObject("parameters")
        val out = mutableMapOf<String, String>()
        val keys = params.keys()
        while (keys.hasNext()) {
            val k = keys.next()
            out[k] = params.getString(k)
        }
        return out
    }

    @Test
    fun `Stripe config emits stripe-shaped tokenization parameters`() {
        val params = tokenizationParameters(
            GatewayConfig.Stripe(publishableKey = "pk_live_x")
        )
        assertEquals("stripe", params["gateway"])
        assertEquals("2018-10-31", params["stripe:version"])
        assertEquals("pk_live_x", params["stripe:publishableKey"])
        assertFalse(params.containsKey("gatewayMerchantId"))
    }

    @Test
    fun `Stripe config with stripeAccountId emits gatewayMerchantId`() {
        val params = tokenizationParameters(
            GatewayConfig.Stripe(publishableKey = "pk_x", stripeAccountId = "acct_1")
        )
        assertEquals("acct_1", params["gatewayMerchantId"])
    }

    @Test
    fun `FatZebra-shaped Custom config emits gateway + gatewayMerchantId`() {
        val params = tokenizationParameters(
            GatewayConfig.Custom(
                gatewayName = "fatzebra",
                gatewayMerchantId = "fz_merchant_xyz",
            )
        )
        assertEquals(
            mapOf("gateway" to "fatzebra", "gatewayMerchantId" to "fz_merchant_xyz"),
            params
        )
    }

    @Test
    fun `Braintree-shaped Custom config emits additionalParameters`() {
        val params = tokenizationParameters(
            GatewayConfig.Custom(
                gatewayName = "braintree",
                additionalParameters = mapOf(
                    "braintree:apiVersion" to "v1",
                    "braintree:clientKey" to "ck_prod",
                )
            )
        )
        assertEquals("braintree", params["gateway"])
        assertEquals("v1", params["braintree:apiVersion"])
        assertEquals("ck_prod", params["braintree:clientKey"])
        assertFalse(params.containsKey("gatewayMerchantId"))
    }

    @Test
    fun `type of tokenizationSpecification is PAYMENT_GATEWAY`() {
        val service = buildService(GatewayConfig.Stripe(publishableKey = "pk_x"))
        val spec = service.paymentDataRequest("10.00")
            .getJSONArray("allowedPaymentMethods")
            .getJSONObject(0)
            .getJSONObject("tokenizationSpecification")
        assertEquals("PAYMENT_GATEWAY", spec.getString("type"))
    }

    @Test
    fun `paymentDataRequest includes merchantInfo and transactionInfo`() {
        val request = buildService(GatewayConfig.Stripe(publishableKey = "pk_x"))
            .paymentDataRequest("10.00")
        assertTrue(request.has("merchantInfo"))
        assertTrue(request.has("transactionInfo"))
    }
}
