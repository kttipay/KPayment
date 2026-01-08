package com.kttipay.payment.internal.googlepay

import android.content.Context
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.kttipay.payment.api.PaymentEnvironment
import com.kttipay.payment.api.config.GooglePayConfig
import com.kttipay.payment.internal.config.GooglePayApiConstants
import org.json.JSONArray
import org.json.JSONObject

/**
 * Implementation of GooglePayService.
 *
 * This class manages Google Pay configuration and request building.
 * It maintains internal state and must be configured before use.
 */
internal class GooglePayServiceImpl : GooglePayService {
    private var state: State? = null

    override fun configure(config: GooglePayConfig, environment: PaymentEnvironment) {
        state = State(config, environment)
    }

    override fun readyToPayRequest(): JSONObject = requireState().requestFactory.readyToPayRequest()

    override fun allowedPaymentMethodsJson(): String =
        requireState().requestFactory.allowedPaymentMethodsJson()

    override fun paymentDataRequest(amount: String): JSONObject =
        requireState().requestFactory.paymentDataRequest(amount)

    override fun createPaymentsClient(context: Context): PaymentsClient =
        requireState().createClient(context)

    override fun isConfigured(): Boolean = state != null

    private fun requireState(): State = state
        ?: error("GooglePayService must be configured before use. Call configure() first.")

    private class State(
        private val config: GooglePayConfig,
        private val environment: PaymentEnvironment
    ) {
        val requestFactory = RequestFactory(config)

        fun createClient(context: Context): PaymentsClient {
            val walletOptions = Wallet.WalletOptions.Builder()
                .setEnvironment(
                    when (environment) {
                        PaymentEnvironment.Production -> WalletConstants.ENVIRONMENT_PRODUCTION
                        PaymentEnvironment.Development -> WalletConstants.ENVIRONMENT_TEST
                    }
                )
                .build()
            return Wallet.getPaymentsClient(context, walletOptions)
        }
    }

    private class RequestFactory(private val config: GooglePayConfig) {
        private val baseRequest = JSONObject().apply {
            put("apiVersion", GooglePayApiConstants.API_VERSION)
            put("apiVersionMinor", GooglePayApiConstants.API_VERSION_MINOR)
        }

        private val allowedCardNetworks: JSONArray
            get() = JSONArray(config.allowedCardNetworks.map { it.value })

        private val allowedCardAuthMethods: JSONArray
            get() = JSONArray(config.allowedAuthMethods.map { it.value })

        private fun baseCardPaymentMethod(): JSONObject {
            return JSONObject().apply {
                val parameters = JSONObject().apply {
                    put("allowedAuthMethods", allowedCardAuthMethods)
                    put("allowedCardNetworks", allowedCardNetworks)
                    put("allowCreditCards", config.allowCreditCards)
                    put("assuranceDetailsRequired", config.assuranceDetailsRequired)
                }
                put("type", GooglePayApiConstants.PAYMENT_TYPE_CARD)
                put("parameters", parameters)
            }
        }

        private fun gatewayTokenizationSpecification(): JSONObject {
            return JSONObject().apply {
                put("type", GooglePayApiConstants.TOKENIZATION_TYPE_GATEWAY)
                put(
                    "parameters",
                    JSONObject(
                        mapOf(
                            "gateway" to config.gateway,
                            "gatewayMerchantId" to config.gatewayMerchantId
                        )
                    )
                )
            }
        }

        private fun cardPaymentMethod(): JSONObject {
            return baseCardPaymentMethod().apply {
                put("tokenizationSpecification", gatewayTokenizationSpecification())
            }
        }

        fun readyToPayRequest(): JSONObject {
            return JSONObject(baseRequest.toString()).apply {
                put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod()))
            }
        }

        fun allowedPaymentMethodsJson(): String {
            return JSONArray().put(cardPaymentMethod()).toString()
        }

        fun paymentDataRequest(amount: String): JSONObject {
            return JSONObject(baseRequest.toString()).apply {
                put("allowedPaymentMethods", JSONArray().put(cardPaymentMethod()))
                put("transactionInfo", transactionInfo(amount))
                put("merchantInfo", merchantInfo())
            }
        }

        private fun transactionInfo(amount: String): JSONObject {
            return JSONObject().apply {
                put("totalPrice", amount)
                put("totalPriceStatus", GooglePayApiConstants.TRANSACTION_PRICE_STATUS_FINAL)
                put("countryCode", config.countryCode)
                put("currencyCode", config.currencyCode)
            }
        }

        private fun merchantInfo(): JSONObject {
            return JSONObject().apply {
                put("merchantId", config.merchantId)
                put("merchantName", config.merchantName)
            }
        }
    }
}
