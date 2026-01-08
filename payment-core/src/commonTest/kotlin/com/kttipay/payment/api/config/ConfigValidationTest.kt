package com.kttipay.payment.api.config

import com.kttipay.payment.api.PaymentEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ConfigValidationTest {
    @Test
    fun `GooglePayConfig can be created with valid values`() {
        val config = GooglePayConfig(
            merchantId = "merchant_123",
            merchantName = "Test Merchant",
            gateway = "stripe",
            gatewayMerchantId = "gateway_123"
        )

        assertNotNull(config)
        assertEquals("merchant_123", config.merchantId)
        assertEquals("Test Merchant", config.merchantName)
    }

    @Test
    fun `GooglePayConfig uses default values`() {
        val config = GooglePayConfig(
            merchantId = "merchant_123",
            merchantName = "Test Merchant",
            gateway = "stripe",
            gatewayMerchantId = "gateway_123"
        )

        assertEquals(setOf(GooglePayCardNetwork.MASTERCARD, GooglePayCardNetwork.VISA), config.allowedCardNetworks)
        assertEquals(GooglePayAuthMethod.DEFAULT, config.allowedAuthMethods)
        assertFalse(config.allowCreditCards)
        assertFalse(config.assuranceDetailsRequired)
        assertEquals("AUD", config.currencyCode)
        assertEquals("AU", config.countryCode)
    }

    @Test
    fun `ApplePayBaseConfig can be created with valid values`() {
        val config = ApplePayBaseConfig(
            merchantName = "Test Merchant"
        )

        assertNotNull(config)
        assertEquals("Test Merchant", config.merchantName)
    }

    @Test
    fun `ApplePayBaseConfig uses default values`() {
        val config = ApplePayBaseConfig(
            merchantName = "Test Merchant"
        )

        val expectedNetworks = setOf(ApplePayNetwork.VISA, ApplePayNetwork.MASTERCARD, ApplePayNetwork.AMEX)
        assertEquals(expectedNetworks, config.supportedNetworks)
        assertEquals(ApplePayMerchantCapability.DEFAULT, config.merchantCapabilities)
        assertEquals("AUD", config.currencyCode)
        assertEquals("AU", config.countryCode)
    }

    @Test
    fun `MobilePaymentConfig can be created with Google Pay only`() {
        val config = MobilePaymentConfig(
            environment = PaymentEnvironment.Development,
            googlePay = GooglePayConfig(
                merchantId = "merchant_123",
                merchantName = "Test Merchant",
                gateway = "stripe",
                gatewayMerchantId = "gateway_123"
            )
        )

        assertNotNull(config.googlePay)
        assertNull(config.applePayMobile)
    }

    @Test
    fun `MobilePaymentConfig can be created with Apple Pay only`() {
        val config = MobilePaymentConfig(
            environment = PaymentEnvironment.Production,
            applePayMobile = ApplePayMobileConfig(
                merchantId = "merchant.com.test",
                base = ApplePayBaseConfig(merchantName = "Test Merchant")
            )
        )

        assertNull(config.googlePay)
        assertNotNull(config.applePayMobile)
    }

    @Test
    fun `WebPaymentConfig can be created with valid values`() {
        val config = WebPaymentConfig(
            environment = PaymentEnvironment.Development,
            googlePay = GooglePayConfig(
                merchantId = "merchant_123",
                merchantName = "Test Merchant",
                gateway = "stripe",
                gatewayMerchantId = "gateway_123"
            ),
            applePayWeb = ApplePayWebConfig(
                base = ApplePayBaseConfig(merchantName = "Test Merchant"),
                merchantValidationEndpoint = "https://example.com/validate",
                baseUrl = "https://example.com",
                domain = "example.com"
            )
        )

        assertNotNull(config.googlePay)
        assertNotNull(config.applePayWeb)
    }
}
