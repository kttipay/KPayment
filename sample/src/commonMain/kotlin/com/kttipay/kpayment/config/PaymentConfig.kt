package com.kttipay.kpayment.config

import com.kttipay.payment.api.config.ApplePayBaseConfig
import com.kttipay.payment.api.config.ApplePayMerchantCapability
import com.kttipay.payment.api.config.ApplePayMobileConfig
import com.kttipay.payment.api.config.ApplePayNetwork
import com.kttipay.payment.api.config.GooglePayConfig

/**
 * Payment configuration for the sample app.
 *
 * Replace these placeholder values with your actual merchant credentials:
 *
 * For Google Pay:
 * - Get your merchant ID from: https://pay.google.com/business/console
 * - Configure your payment gateway integration
 * - Use "TEST" environment for development, "PRODUCTION" for release
 *
 * For Apple Pay:
 * - Create merchant ID in: https://developer.apple.com/account/resources/identifiers/list/merchant
 * - Format: merchant.com.yourcompany.yourapp
 * - Configure merchant identity certificate in Xcode
 */
object PaymentConfig {

    /**
     * Google Pay merchant name displayed during payment.
     * Replace with your actual business name.
     */
    const val GOOGLE_PAY_MERCHANT_NAME = "Your Store Name"

    /**
     * Google Pay gateway merchant ID.
     * Get this from your payment gateway provider (e.g., Stripe, Braintree, etc.)
     */
    const val GOOGLE_PAY_GATEWAY_MERCHANT_ID = "your_gateway_merchant_id"

    /**
     * Payment gateway identifier.
     * Examples: "stripe", "braintree", "checkout", etc.
     */
    const val GOOGLE_PAY_GATEWAY = "example"

    /**
     * Google Pay environment.
     * Use "TEST" for development/testing, "PRODUCTION" for live payments.
     */
    const val GOOGLE_PAY_ENVIRONMENT = "TEST"

    // MARK: - Apple Pay Configuration

    /**
     * Apple Pay merchant identifier.
     * Create this in your Apple Developer account.
     * Format: merchant.com.yourcompany.yourapp
     */
    const val APPLE_PAY_MERCHANT_ID = ""

    // MARK: - Common Payment Configuration

    /**
     * Test payment amount in the currency's smallest unit.
     * For demonstration purposes.
     */
    const val PAYMENT_AMOUNT = 1.00

    /**
     * Currency code (ISO 4217).
     * Examples: "USD", "EUR", "GBP", "AUD", etc.
     */
    const val CURRENCY_CODE = "AUD"

    /**
     * Country code (ISO 3166-1 alpha-2).
     * Examples: "US", "GB", "AU", "CA", etc.
     */
    const val COUNTRY_CODE = "AU"

    /**
     * Allowed card networks for payments.
     */
    val ALLOWED_CARD_NETWORKS = listOf(
        "VISA",
        "MASTERCARD",
        "AMEX",
        "DISCOVER"
    )

    /**
     * Apple Pay supported networks.
     */
    val APPLE_PAY_NETWORKS = setOf(
        ApplePayNetwork.VISA,
        ApplePayNetwork.MASTERCARD,
        ApplePayNetwork.AMEX,
        ApplePayNetwork.DISCOVER
    )

    /**
     * Apple Pay merchant capabilities.
     */
    val APPLE_PAY_MERCHANT_CAPABILITIES = setOf(
        ApplePayMerchantCapability.CAPABILITY_3DS,
        ApplePayMerchantCapability.CAPABILITY_DEBIT,
        ApplePayMerchantCapability.CAPABILITY_CREDIT
    )

    /**
     * Creates a Google Pay configuration with current settings.
     */
    fun createGooglePayConfig(
        currencyCode: String = CURRENCY_CODE,
        countryCode: String = COUNTRY_CODE
    ): GooglePayConfig {
        return GooglePayConfig(
            merchantId = "",
            merchantName = GOOGLE_PAY_MERCHANT_NAME,
            gatewayMerchantId = GOOGLE_PAY_GATEWAY_MERCHANT_ID,
            gateway = GOOGLE_PAY_GATEWAY,
            currencyCode = currencyCode,
            countryCode = countryCode,
            allowedCardNetworks = ALLOWED_CARD_NETWORKS,
            allowedAuthMethods = listOf("PAN_ONLY", "CRYPTOGRAM_3DS"),
        )
    }

    /**
     * Creates an Apple Pay configuration with current settings.
     */
    fun createApplePayConfig(
        currencyCode: String = CURRENCY_CODE,
        countryCode: String = COUNTRY_CODE
    ): ApplePayMobileConfig {
        return ApplePayMobileConfig(
            merchantId = APPLE_PAY_MERCHANT_ID,
            base = ApplePayBaseConfig(
                merchantName = "KTTIPAY PTY LTD",
                currencyCode = currencyCode,
                countryCode = countryCode,
                supportedNetworks = APPLE_PAY_NETWORKS,
                merchantCapabilities = APPLE_PAY_MERCHANT_CAPABILITIES
            ),
        )
    }
}
