package com.kttipay.payment.internal.googlepay

import android.content.Context
import com.google.android.gms.wallet.PaymentsClient
import com.kttipay.payment.api.PaymentEnvironment
import com.kttipay.payment.api.config.GooglePayConfig
import org.json.JSONObject
import org.kimplify.deci.Deci

/**
 * Service for managing Google Pay integration.
 *
 * This service handles configuration, availability checking, and request building
 * for Google Pay payments. It must be configured before use via [configure].
 */
internal interface GooglePayService {
    /**
     * Configures the Google Pay service with the given config and environment.
     *
     * This must be called before any other operations.
     *
     * @param config Google Pay configuration
     * @param environment Payment environment (Production or Development)
     */
    fun configure(config: GooglePayConfig, environment: PaymentEnvironment)

    /**
     * Creates a ready-to-pay request for checking if Google Pay is available.
     *
     * @return JSON object representing the ready-to-pay request
     * @throws IllegalStateException if service is not configured
     */
    fun readyToPayRequest(): JSONObject

    /**
     * Returns JSON string of allowed payment methods.
     *
     * @return JSON string for allowed payment methods
     * @throws IllegalStateException if service is not configured
     */
    fun allowedPaymentMethodsJson(): String

    /**
     * Creates a payment data request for the given amount.
     *
     * @param amount Payment amount
     * @return JSON object representing the payment request
     * @throws IllegalStateException if service is not configured
     */
    fun paymentDataRequest(amount: Deci): JSONObject

    /**
     * Creates a PaymentsClient for interacting with Google Pay.
     *
     * @param context Android context
     * @return Configured PaymentsClient
     * @throws IllegalStateException if service is not configured
     */
    fun createPaymentsClient(context: Context): PaymentsClient

    /**
     * Checks if the service has been configured.
     *
     * @return true if configure() has been called, false otherwise
     */
    fun isConfigured(): Boolean
}