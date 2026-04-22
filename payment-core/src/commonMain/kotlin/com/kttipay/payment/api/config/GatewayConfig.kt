package com.kttipay.payment.api.config

/**
 * Tokenization gateway configuration for Google Pay.
 *
 * Google Pay's `tokenizationSpecification.parameters` shape is gateway-specific. Pick the
 * variant that matches your processor:
 *
 * - [Stripe] — first-class, typed. Stripe requires `publishableKey` + `apiVersion`;
 *   `stripeAccountId` is optional (Stripe Connect connected account ID).
 * - [Custom] — escape hatch for any other gateway (FatZebra, Braintree, Adyen, Checkout.com, ...).
 *   Consult your gateway's Google Pay integration docs for the exact parameter keys.
 */
sealed class GatewayConfig {

    /**
     * Google Pay's Stripe gateway integration.
     *
     * Emits tokenization parameters:
     * ```
     * { gateway: "stripe", stripe:version: <apiVersion>, stripe:publishableKey: <publishableKey>,
     *   gatewayMerchantId?: <stripeAccountId> }
     * ```
     *
     * @param publishableKey Stripe publishable key (e.g. `pk_live_...` or `pk_test_...`).
     * @param apiVersion Stripe's Google Pay API version. Defaults to `2018-10-31` per Stripe's docs.
     * @param stripeAccountId Optional Stripe Connect connected account ID (e.g. `acct_...`).
     */
    data class Stripe(
        val publishableKey: String,
        val apiVersion: String = "2018-10-31",
        val stripeAccountId: String? = null,
    ) : GatewayConfig() {
        init {
            require(publishableKey.isNotBlank()) { "publishableKey cannot be blank" }
            require(apiVersion.isNotBlank()) { "apiVersion cannot be blank" }
            stripeAccountId?.let {
                require(it.isNotBlank()) { "stripeAccountId cannot be blank if provided" }
            }
        }
    }

    /**
     * Any other Google Pay gateway. Consult the gateway's docs for required parameters.
     *
     * @param gatewayName Lowercase gateway identifier as recognized by Google Pay
     *                    (e.g. "fatzebra", "braintree", "adyen", "checkoutltd").
     * @param gatewayMerchantId Google Pay's common merchant-id parameter. Used by FatZebra, Adyen,
     *                          and several others. Not used by Braintree.
     * @param additionalParameters Gateway-specific parameters (e.g. `"braintree:clientKey"`).
     */
    data class Custom(
        val gatewayName: String,
        val gatewayMerchantId: String? = null,
        val additionalParameters: Map<String, String> = emptyMap(),
    ) : GatewayConfig() {
        init {
            require(gatewayName.isNotBlank()) { "gatewayName cannot be blank" }
            require(gatewayMerchantId != null || additionalParameters.isNotEmpty()) {
                "Custom gateway needs gatewayMerchantId or additionalParameters (one or both)"
            }
            gatewayMerchantId?.let {
                require(it.isNotBlank()) { "gatewayMerchantId cannot be blank if provided" }
            }
            additionalParameters.forEach { (k, v) ->
                require(k.isNotBlank()) { "parameter keys cannot be blank" }
                require(v.isNotBlank()) { "parameter values cannot be blank (key: $k)" }
            }
            require(
                !additionalParameters.containsKey("gateway") &&
                    !additionalParameters.containsKey("gatewayMerchantId")
            ) {
                "additionalParameters cannot contain reserved keys 'gateway' or 'gatewayMerchantId' — use the typed fields instead"
            }
        }
    }
}
