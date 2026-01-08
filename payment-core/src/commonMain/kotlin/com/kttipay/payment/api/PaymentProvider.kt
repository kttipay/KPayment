package com.kttipay.payment.api

/**
 * Enumeration of supported payment providers.
 *
 * Use these values to specify which payment provider to use or check capabilities.
 *
 * Example usage:
 * ```
 * val canUseGooglePay = manager.canUse(PaymentProvider.GooglePay)
 * val canUseApplePay = manager.canUse(PaymentProvider.ApplePay)
 *
 * manager.observeAvailability(PaymentProvider.GooglePay).collect { available ->
 *     if (available) {
 *     }
 * }
 * ```
 */
sealed interface PaymentProvider {
    /**
     * Google Pay payment provider.
     * Available on Android and Web platforms.
     */
    data object GooglePay : PaymentProvider

    /**
     * Apple Pay payment provider.
     * Available on iOS and Safari (Web) platforms.
     */
    data object ApplePay : PaymentProvider
}
