package com.kttipay.payment.api.config

import androidx.compose.runtime.Stable
import com.kttipay.payment.api.PaymentEnvironment

/**
 * Base interface for platform-specific payment configurations.
 *
 * Each platform (Mobile or Web) has its own configuration implementation that extends this interface.
 * At least one payment provider (Google Pay or Apple Pay) must be configured.
 */
sealed interface PlatformPaymentConfig {
    /**
     * The payment environment to use.
     * Use [PaymentEnvironment.Development] for testing, [PaymentEnvironment.Production] for live payments.
     */
    val environment: PaymentEnvironment

    /**
     * Google Pay configuration, or null if Google Pay is not configured.
     * Configure this to enable Google Pay on the platform.
     */
    val googlePay: GooglePayConfig?

    /**
     * Apple Pay configuration, or null if Apple Pay is not configured.
     * Configure this to enable Apple Pay on the platform.
     */
    val applePay: ApplePayConfig?
}

/**
 * Configuration for mobile payment platforms (Android and iOS).
 *
 * Use this configuration when creating a [MobilePaymentManager] for Android or iOS apps.
 * You can configure Google Pay (Android), Apple Pay (iOS), or both.
 *
 * @param environment The payment environment (Development or Production).
 * @param googlePay Google Pay configuration for Android. Set to null if not using Google Pay.
 * @param applePayMobile Apple Pay configuration for iOS. Set to null if not using Apple Pay.
 *
 * Example usage:
 * ```
 * val mobileConfig = MobilePaymentConfig(
 *     environment = PaymentEnvironment.Development,
 *     googlePay = GooglePayConfig(
 *         merchantId = "YOUR_MERCHANT_ID",
 *         merchantName = "Your Store",
 *         gateway = "stripe",
 *         gatewayMerchantId = "YOUR_GATEWAY_ID"
 *     ),
 *     applePayMobile = ApplePayMobileConfig(
 *         merchantId = "merchant.com.yourcompany.app",
 *         base = ApplePayBaseConfig(merchantName = "Your Store")
 *     )
 * )
 * ```
 */
@Stable
data class MobilePaymentConfig(
    override val environment: PaymentEnvironment,
    override val googlePay: GooglePayConfig? = null,
    val applePayMobile: ApplePayMobileConfig? = null
) : PlatformPaymentConfig {
    override val applePay: ApplePayConfig?
        get() = applePayMobile

    init {
        require(googlePay != null || applePayMobile != null) {
            "At least one payment provider (Google Pay or Apple Pay) must be configured"
        }
    }
}

/**
 * Configuration for web payment platforms (JS and WASM).
 *
 * Use this configuration when creating a [WebPaymentManager] for web applications.
 * You can configure Google Pay, Apple Pay (Safari), or both.
 *
 * @param environment The payment environment (Development or Production).
 * @param googlePay Google Pay configuration for web. Set to null if not using Google Pay.
 * @param applePayWeb Apple Pay configuration for Safari. Set to null if not using Apple Pay.
 *                    Note: Apple Pay on web requires Safari and domain validation.
 *
 * Example usage:
 * ```
 * val webConfig = WebPaymentConfig(
 *     environment = PaymentEnvironment.Development,
 *     googlePay = GooglePayConfig(
 *         merchantId = "YOUR_MERCHANT_ID",
 *         merchantName = "Your Store",
 *         gateway = "stripe",
 *         gatewayMerchantId = "YOUR_GATEWAY_ID"
 *     ),
 *     applePayWeb = ApplePayWebConfig(
 *         base = ApplePayBaseConfig(merchantName = "Your Store"),
 *         merchantValidationEndpoint = "https://example.com/apple-pay/validate",
 *         baseUrl = "https://example.com",
 *         domain = "example.com"
 *     )
 * )
 * ```
 */
@Stable
data class WebPaymentConfig(
    override val environment: PaymentEnvironment,
    override val googlePay: GooglePayConfig? = null,
    val applePayWeb: ApplePayWebConfig? = null
) : PlatformPaymentConfig {
    override val applePay: ApplePayConfig?
        get() = applePayWeb

    init {
        require(googlePay != null || applePayWeb != null) {
            "At least one payment provider (Google Pay or Apple Pay) must be configured"
        }
    }
}
