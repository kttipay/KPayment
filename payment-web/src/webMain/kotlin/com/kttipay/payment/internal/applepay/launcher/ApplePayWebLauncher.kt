package com.kttipay.payment.internal.applepay.launcher

import com.kttipay.payment.api.PaymentErrorReason
import com.kttipay.payment.api.PaymentLauncher
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult
import com.kttipay.payment.api.config.ApplePayWebConfig
import com.kttipay.payment.internal.applepay.ApplePaySessionManager
import com.kttipay.payment.internal.applepay.ApplePayWebErrorCode
import com.kttipay.payment.internal.applepay.ApplePayWebResult
import com.kttipay.payment.internal.applepay.createApplePaySession
import com.kttipay.payment.internal.applepay.parseJsonToJs
import com.kttipay.payment.internal.applepay.toPaymentResult
import com.kttipay.payment.internal.config.ApplePayApiConstants
import com.kttipay.payment.internal.logging.KPaymentLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val onResult: (PaymentResult) -> Unit
) : PaymentLauncher {

    override val provider: PaymentProvider = PaymentProvider.ApplePay

    private val _isProcessing = MutableStateFlow(false)
    override val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val logger = KPaymentLogger.tag("ApplePayWebLauncher")

    override fun launch(amount: String) {
        if (!_isProcessing.compareAndSet(expect = false, update = true)) {
            onResult(
                PaymentResult.Error(
                    provider = provider,
                    reason = PaymentErrorReason.AlreadyInProgress,
                    message = "A payment is already in progress"
                )
            )
            return
        }

        val paymentRequestJson = buildPaymentRequest(amount)
        val paymentRequestJs = parseJsonToJs(paymentRequestJson)

        startApplePayFlow(
            version = ApplePayApiConstants.API_VERSION,
            paymentRequest = paymentRequestJs,
            config = config,
            onPaymentAuthorized = { token ->
                _isProcessing.value = false
                onResult(ApplePayWebResult.Success(token).toPaymentResult())
            },
            onError = { errorCode, errorMessage ->
                _isProcessing.value = false
                onResult(ApplePayWebResult.Failure(errorCode, errorMessage).toPaymentResult())
            },
            onCancel = {
                _isProcessing.value = false
                onResult(ApplePayWebResult.Cancelled.toPaymentResult())
            }
        )
    }

    private fun buildPaymentRequest(amount: String): String {
        val jsonRequest = buildJsonObject {
            put("countryCode", config.countryCode)
            put("currencyCode", config.currencyCode)
            putJsonArray("supportedNetworks") {
                config.supportedNetworks.map { JsonPrimitive(it.value) }.forEach(::add)
            }
            putJsonArray("merchantCapabilities") {
                config.merchantCapabilities.map { JsonPrimitive(it.value) }.forEach(::add)
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
        onError: (ApplePayWebErrorCode, String?) -> Unit,
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
            onError(ApplePayWebErrorCode.SESSION_BEGIN_FAILED, error.message)
        }
    }
}
