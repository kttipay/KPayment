package com.kttipay.kpayment

import com.kttipay.payment.api.PaymentEnvironment
import com.kttipay.payment.api.config.ApplePayBaseConfig
import com.kttipay.payment.api.config.ApplePayMerchantCapability
import com.kttipay.payment.api.config.ApplePayMobileConfig
import com.kttipay.payment.api.config.ApplePayNetwork
import com.kttipay.payment.api.config.ApplePayWebConfig
import com.kttipay.payment.api.config.GooglePayAuthMethod
import com.kttipay.payment.api.config.GooglePayCardNetwork
import com.kttipay.payment.api.config.GooglePayConfig
import com.kttipay.payment.api.config.WebPaymentConfig
import com.kttipay.payment.api.validation.ConfigResult
import com.kttipay.payment.api.validation.PaymentConfigValidator
import com.kttipay.payment.api.validation.map

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
     * Google Pay merchant identifier.
     * Replace with your actual Google Merchant ID.
     */
    const val GOOGLE_PAY_MERCHANT_ID = "YOUR_MERCHANT_ID_HERE"

    /**
     * Google Pay merchant name displayed during payment.
     * Replace with your actual business name.
     */
    const val GOOGLE_PAY_MERCHANT_NAME = "YOUR_MERCHANT_NAME_HERE"

    /**
     * Google Pay merchant ID.
     */
    const val GOOGLE_PAY_MERCHANT_ID = "YOUR_MERCHANT_ID_HERE"

    /**
     * Google Pay gateway merchant ID.
     * Get this from your payment gateway provider (e.g., Stripe, Braintree, etc.)
     */
    const val GOOGLE_PAY_GATEWAY_MERCHANT_ID = "YOUR_GATEWAY_MERCHANT_ID_HERE"

    /**
     * Payment gateway identifier.
     * Examples: "stripe", "braintree", "checkout", etc.
     */
    const val GOOGLE_PAY_GATEWAY = "stripe"

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
    const val APPLE_PAY_MERCHANT_ID = "merchant.com.yourcompany.yourapp"

    /**
     * Apple Pay merchant validation endpoint exposed by your backend.
     * This is required for both Web and iOS.
     */
    const val APPLE_PAY_MERCHANT_VALIDATION_ENDPOINT = "https://your-backend.com/apple-pay/validate"

    /**
     * Base URL of your hosted payment page/backend, used by Apple Pay on Web.
     */
    const val APPLE_PAY_BASE_URL = "https://your-backend.com"

    /**
     * Domain where the Apple Pay JS integration is hosted.
     */
    const val APPLE_PAY_DOMAIN = "localhost"

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
     * Google Pay allowed card networks.
     */
    val GOOGLE_PAY_CARD_NETWORKS = setOf(
        GooglePayCardNetwork.VISA,
        GooglePayCardNetwork.MASTERCARD,
        GooglePayCardNetwork.AMEX,
        GooglePayCardNetwork.DISCOVER
    )

    /**
     * Google Pay authentication methods.
     */
    val GOOGLE_PAY_AUTH_METHODS = GooglePayAuthMethod.DEFAULT

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
            merchantId = GOOGLE_PAY_MERCHANT_ID,
            merchantName = GOOGLE_PAY_MERCHANT_NAME,
            gatewayMerchantId = GOOGLE_PAY_GATEWAY_MERCHANT_ID,
            gateway = GOOGLE_PAY_GATEWAY,
            currencyCode = currencyCode,
            countryCode = countryCode,
            allowedCardNetworks = GOOGLE_PAY_CARD_NETWORKS,
            allowedAuthMethods = GOOGLE_PAY_AUTH_METHODS
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

    /**
     * Creates an Apple Pay Web configuration with current settings.
     */
    fun createApplePayWebConfig(
        currencyCode: String = CURRENCY_CODE,
        countryCode: String = COUNTRY_CODE
    ): ApplePayWebConfig {
        return ApplePayWebConfig(
            base = ApplePayBaseConfig(
                merchantName = "KTTIPAY PTY LTD",
                currencyCode = currencyCode,
                countryCode = countryCode,
                supportedNetworks = APPLE_PAY_NETWORKS,
                merchantCapabilities = APPLE_PAY_MERCHANT_CAPABILITIES
            ),
            merchantValidationEndpoint = APPLE_PAY_MERCHANT_VALIDATION_ENDPOINT,
            baseUrl = "https://${kotlinx.browser.window.location.hostname}",
            domain = kotlinx.browser.window.location.hostname
        )
    }

    /**
     * Combined Web payment config helper for the web sample targets.
     */
    fun createWebPaymentConfig(
        environment: PaymentEnvironment = PaymentEnvironment.Development,
        currencyCode: String = CURRENCY_CODE,
        countryCode: String = COUNTRY_CODE
    ): WebPaymentConfig {
        return WebPaymentConfig(
            environment = environment,
            googlePay = createGooglePayConfig(currencyCode, countryCode),
            applePayWeb = APPLE_PAY_MERCHANT_VALIDATION_ENDPOINT.takeIf { it.isNotBlank() }
                ?.let { createApplePayWebConfig(currencyCode, countryCode) }
        )
    }

    /**
     * Builds a Google Pay configuration after validation.
     * Returns a type-safe ConfigResult.
     *
     * @return ConfigResult with either a valid config or validation errors
     */
    fun buildGooglePayConfig(): ConfigResult<GooglePayConfig> {
        return PaymentConfigValidator.validateGooglePayValues(
            merchantId = GOOGLE_PAY_MERCHANT_ID,
            merchantName = GOOGLE_PAY_MERCHANT_NAME,
            gatewayMerchantId = GOOGLE_PAY_GATEWAY_MERCHANT_ID
        ).map { createGooglePayConfig() }
    }

    /**
     * Builds an Apple Pay web configuration after validation.
     * Returns a type-safe ConfigResult.
     *
     * @return ConfigResult with either a valid config or validation errors
     */
    fun buildApplePayWebConfig(): ConfigResult<ApplePayWebConfig> {
        return PaymentConfigValidator.validateApplePayWebValues(
            merchantValidationEndpoint = APPLE_PAY_MERCHANT_VALIDATION_ENDPOINT
        ).map { createApplePayWebConfig() }
    }
}
