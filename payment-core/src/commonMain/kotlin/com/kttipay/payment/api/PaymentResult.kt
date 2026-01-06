package com.kttipay.payment.api

/**
 * Result of a payment request operation.
 *
 * This sealed interface represents the possible outcomes when initiating a payment:
 * - [Success]: Payment was successful and a token was received
 * - [Cancelled]: User cancelled the payment flow
 * - [Error]: An error occurred during payment processing
 *
 * Example usage:
 * ```
 * when (val result = paymentResult) {
 *     is PaymentResult.Success -> {
 *         val token = result.token
 *     }
 *     is PaymentResult.Cancelled -> {
 *     }
 *     is PaymentResult.Error -> {
 *         when (result.reason) {
 *             PaymentErrorReason.NetworkError -> {
 *             }
 *             PaymentErrorReason.NotAvailable -> {
 *             }
 *             else -> {
 *             }
 *         }
 *     }
 * }
 * ```
 */
sealed interface PaymentResult {
    /**
     * The payment provider that was used (Google Pay or Apple Pay).
     */
    val provider: PaymentProvider

    /**
     * Successful payment result containing the payment token.
     *
     * @param provider The payment provider used (Google Pay or Apple Pay).
     * @param token The payment token string. This token should be sent to your backend
     *              for processing. The format depends on the payment provider and gateway.
     */
    data class Success(
        override val provider: PaymentProvider,
        val token: String
    ) : PaymentResult

    /**
     * Payment was cancelled by the user.
     *
     * @param provider The payment provider that was cancelled.
     */
    data class Cancelled(
        override val provider: PaymentProvider
    ) : PaymentResult

    /**
     * Payment failed due to an error.
     *
     * @param provider The payment provider that encountered the error.
     * @param reason The specific error reason. Defaults to [PaymentErrorReason.Unknown].
     * @param message Optional error message providing additional details about the error.
     */
    data class Error(
        override val provider: PaymentProvider,
        val reason: PaymentErrorReason = PaymentErrorReason.Unknown,
        val message: String? = null
    ) : PaymentResult
}

/**
 * Enumeration of possible payment error reasons.
 *
 * Use these values to determine the cause of a payment failure and handle errors appropriately.
 */
enum class PaymentErrorReason {
    /**
     * The payment request timed out.
     * Consider retrying the payment request.
     */
    Timeout,

    /**
     * The payment API is not connected or initialized.
     * Ensure the payment manager is properly configured and initialized.
     */
    ApiNotConnected,

    /**
     * The connection was suspended during the payment call.
     * This may occur if the app was backgrounded or the network connection was interrupted.
     */
    ConnectionSuspendedDuringCall,

    /**
     * A developer error occurred, typically due to misconfiguration.
     * Check your payment configuration and ensure all required parameters are set correctly.
     */
    DeveloperError,

    /**
     * An internal error occurred in the payment system.
     * This is typically a temporary issue. Consider retrying after a delay.
     */
    InternalError,

    /**
     * The payment operation was interrupted.
     * This may occur if the user navigated away or the app was closed.
     */
    Interrupted,

    /**
     * A network error occurred during payment processing.
     * Check the network connection and retry the payment.
     */
    NetworkError,

    /**
     * User sign-in is required to complete the payment.
     * Prompt the user to sign in and retry the payment.
     */
    SignInRequired,

    /**
     * The payment method is not available on this device or platform.
     * Check payment capabilities before attempting to process payment.
     */
    NotAvailable,

    /**
     * An unknown error occurred.
     * Check the error message for additional details if available.
     */
    Unknown
}
