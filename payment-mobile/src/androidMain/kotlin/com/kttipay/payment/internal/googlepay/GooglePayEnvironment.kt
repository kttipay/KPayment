package com.kttipay.payment.internal.googlepay

import android.content.Context
import com.google.android.gms.wallet.PaymentsClient
import com.kttipay.payment.api.PaymentEnvironment
import com.kttipay.payment.api.config.GooglePayConfig
import com.kttipay.payment.getSharedGooglePayService
import org.json.JSONObject
import org.kimplify.deci.Deci

/**
 * Facade for GooglePayService that maintains backward compatibility.
 *
 * This object delegates all calls to the shared GooglePayService instance
 * used by the MobilePaymentManager. It exists for backward compatibility
 * with code that directly uses GooglePayEnvironment.
 *
 * @deprecated Use GooglePayService directly via dependency injection.
 * This facade will be removed in a future version.
 */
@Deprecated(
    message = "Use GooglePayService with dependency injection instead",
    level = DeprecationLevel.WARNING
)
internal object GooglePayEnvironment {

    /**
     * Gets the shared GooglePayService instance.
     * This is the same instance used by MobilePaymentManager.
     */
    private fun getService(): GooglePayService = getSharedGooglePayService()

    /**
     * Configures Google Pay with the given config and environment.
     *
     * @param config Google Pay configuration
     * @param environment Payment environment (Production or Development)
     */
    fun configure(config: GooglePayConfig, environment: PaymentEnvironment) {
        getService().configure(config, environment)
    }

    /**
     * Creates a ready-to-pay request for checking Google Pay availability.
     *
     * @return JSON object for ready-to-pay request
     */
    fun readyToPayRequest(): JSONObject = getService().readyToPayRequest()

    /**
     * Returns JSON string of allowed payment methods.
     *
     * @return JSON string for allowed payment methods
     */
    fun allowedPaymentMethodsJson(): String = getService().allowedPaymentMethodsJson()

    /**
     * Creates a payment data request for the given amount.
     *
     * @param amount Payment amount
     * @return JSON object for payment request
     */
    fun paymentDataRequest(amount: Deci): JSONObject = getService().paymentDataRequest(amount)

    /**
     * Creates a PaymentsClient for interacting with Google Pay.
     *
     * @param context Android context
     * @return Configured PaymentsClient
     */
    fun createPaymentsClient(context: Context): PaymentsClient = getService().createPaymentsClient(context)
}
