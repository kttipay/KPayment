package com.kttipay.payment.api

import kotlinx.coroutines.flow.StateFlow

/**
 * Handles payment processing for a specific payment provider.
 *
 * This interface provides a unified API for launching payment flows across different
 * payment providers (Google Pay, Apple Pay, etc.) and monitoring their processing state.
 */
interface PaymentLauncher {
    /**
     * The payment provider this launcher handles.
     */
    val provider: PaymentProvider

    /**
     * Indicates whether a payment is currently being processed.
     *
     * Emits `true` when a payment is in progress, `false` otherwise.
     * Use this to prevent concurrent payment launches or show loading UI.
     */
    val isProcessing: StateFlow<Boolean>

    /**
     * Launches the payment flow for the specified amount.
     *
     * @param amount The payment amount as a string (e.g., "10.00").
     *               Must be a valid decimal number with up to 2 decimal places.
     *
     * If a payment is already in progress, onResult will emit a [PaymentResult.Error]
     * with [PaymentErrorReason.AlreadyInProgress] instead of launching a new payment.
     */
    fun launch(amount: String)
}
