package com.kttipay.payment.internal.applepay.launcher

import org.kimplify.cedar.logging.Cedar
import com.kttipay.common.deci.Deci
import com.kttipay.payment.api.config.ApplePayWebConfig
import com.kttipay.payment.internal.applepay.ApplePaySessionManager
import com.kttipay.payment.internal.applepay.ApplePayWebResult
import com.kttipay.payment.internal.applepay.createApplePaySession
import com.kttipay.payment.internal.applepay.parseJsonToJs
import com.kttipay.payment.internal.config.ApplePayApiConstants
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny


@OptIn(ExperimentalWasmJsInterop::class)
internal class ApplePayWebLauncher(
    private val config: ApplePayWebConfig,
    private val onResult: (ApplePayWebResult) -> Unit
) : IApplePayWebLauncher {

    private val logger = Cedar.tag("ApplePayWebLauncher")

    override fun launch(amount: Deci) {
        val paymentRequestJson = buildPaymentRequest(amount.toString())
        val paymentRequestJs = parseJsonToJs(paymentRequestJson)

        startApplePayFlow(
            version = ApplePayApiConstants.API_VERSION,
            paymentRequest = paymentRequestJs,
            config = config,
            onPaymentAuthorized = { token ->
                onResult(ApplePayWebResult.Success(token))
            },
            onError = {
                onResult(ApplePayWebResult.Failure)
            },
            onCancel = {
                onResult(ApplePayWebResult.Cancelled)
            }
        )
    }

    private fun buildPaymentRequest(amount: String): String {
        val jsonRequest = buildJsonObject {
            put("countryCode", config.countryCode)
            put("currencyCode", config.currencyCode)
            putJsonArray("supportedNetworks") {
                config.supportedNetworks.map { JsonPrimitive(it) }.forEach(::add)
            }
            putJsonArray("merchantCapabilities") {
                config.merchantCapabilities.map { JsonPrimitive(it) }.forEach(::add)
            }
            putJsonObject("total") {
                put("label", config.merchantName)
                put("amount", amount)
            }
        }

        val jsonString = Json.encodeToString(JsonElement.serializer(), jsonRequest)
        logger.d("Apple Pay request JSON: $jsonString")
        return jsonString
    }

    @OptIn(ExperimentalWasmJsInterop::class)
    private fun startApplePayFlow(
        version: Int,
        paymentRequest: JsAny,
        config: ApplePayWebConfig,
        onPaymentAuthorized: (String) -> Unit,
        onError: () -> Unit,
        onCancel: () -> Unit
    ) {
        val sessionManager = ApplePaySessionManager(config)
        val session = createApplePaySession(version, paymentRequest)

        logger.d("Starting Apple Pay session")

        sessionManager.setupSessionHandlers(
            session = session,
            onPaymentAuthorized = onPaymentAuthorized,
            onError = onError,
            onCancel = onCancel
        )

        runCatching {
            session.begin()
        }.onFailure { error ->
            logger.e("Session begin failed", error)
            session.abort()
            onError()
        }
    }
}
