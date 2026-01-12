package com.kttipay.payment.capability

import com.kttipay.payment.api.PaymentProvider

/**
 * Status of payment provider capability checking.
 *
 * This sealed interface represents the various states a payment provider can be in
 * when checking availability. Use [isReady] to quickly check if a provider is ready.
 *
 * Example usage:
 * ```
 * when (val status = capabilities.googlePay) {
 *     is CapabilityStatus.Ready -> {
 *     }
 *     is CapabilityStatus.NotConfigured -> {
 *     }
 *     is CapabilityStatus.NotSupported -> {
 *     }
 *     is CapabilityStatus.Checking -> {
 *     }
 *     is CapabilityStatus.Error -> {
 *         val errorMessage = status.reason
 *     }
 * }
 * ```
 */
sealed interface CapabilityStatus {
    /**
     * Payment provider is not configured.
     * The provider was not included in the payment configuration.
     */
    data object NotConfigured : CapabilityStatus

    /**
     * Capability check is currently in progress.
     * Wait for the check to complete before attempting to use the provider.
     */
    data object Checking : CapabilityStatus

    /**
     * Payment provider is not supported on this platform.
     * For example, Apple Pay is not supported on Android.
     */
    data object NotSupported : CapabilityStatus

    /**
     * Payment provider is ready to use.
     * You can proceed with payment requests using this provider.
     */
    data object Ready : CapabilityStatus

    /**
     * An error occurred while checking capabilities.
     *
     * @param reason Human-readable error message describing what went wrong.
     * @param throwable Optional throwable that caused the error, if available.
     */
    data class Error(val reason: String, val throwable: Throwable? = null) : CapabilityStatus

    /**
     * Returns true if the provider is ready to use, false otherwise.
     */
    val isReady: Boolean
        get() = this == Ready
}

/**
 * Current payment capabilities for all configured providers.
 *
 * This data class holds the capability status for both Google Pay and Apple Pay.
 * Use [statusFor] to get the status for a specific provider, or [canPayWith] to check
 * if a provider is ready to use.
 *
 * @param googlePay Capability status for Google Pay. Defaults to [CapabilityStatus.NotConfigured].
 * @param applePay Capability status for Apple Pay. Defaults to [CapabilityStatus.NotConfigured].
 *
 * Example usage:
 * ```
 * val capabilities = manager.checkCapabilities()
 *
 * if (capabilities.canPayWith(PaymentProvider.GooglePay)) {
 *     launcher.launch("10.00")
 * }
 *
 * val googlePayStatus = capabilities.statusFor(PaymentProvider.GooglePay)
 * ```
 */
data class PaymentCapabilities(
    val googlePay: CapabilityStatus = CapabilityStatus.NotConfigured,
    val applePay: CapabilityStatus = CapabilityStatus.NotConfigured
) {
    companion object {
        /**
         * Initial capabilities state before any checking has occurred.
         *
         * Both providers are in [CapabilityStatus.Checking] state, indicating
         * that capability checking has not yet completed.
         *
         * Use this as the initial value for reactive flows:
         * ```
         * val capabilities by manager.observeCapabilities()
         *     .collectAsState(initial = PaymentCapabilities.initial)
         * ```
         */
        val initial: PaymentCapabilities = PaymentCapabilities(
            googlePay = CapabilityStatus.Checking,
            applePay = CapabilityStatus.Checking
        )
    }
    /**
     * Returns the capability status for the specified payment provider.
     *
     * @param provider The payment provider to check.
     * @return The capability status for the provider.
     */
    fun statusFor(provider: PaymentProvider): CapabilityStatus = when (provider) {
        PaymentProvider.GooglePay -> googlePay
        PaymentProvider.ApplePay -> applePay
    }

    /**
     * Returns true if the specified payment provider is ready to use, false otherwise.
     *
     * @param provider The payment provider to check.
     * @return true if the provider is ready, false otherwise.
     */
    fun canPayWith(provider: PaymentProvider): Boolean {
        return statusFor(provider) == CapabilityStatus.Ready
    }
}
