package com.kttipay.payment.internal.applepay

import com.kttipay.payment.api.config.ApplePayWebConfig
import com.kttipay.payment.internal.logging.KPaymentLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.JsFun
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.unsafeCast

private const val TAG = "ApplePaySessionManager"

internal interface MerchantValidationOrchestrator {
    @OptIn(ExperimentalWasmJsInterop::class)
    suspend fun validate(validationUrl: String, domain: String): JsAny
}

@OptIn(ExperimentalWasmJsInterop::class)
internal class ApplePayMerchantValidationOrchestrator(
    private val config: ApplePayWebConfig,
    private val httpClient: ApplePayHttpClient
) : MerchantValidationOrchestrator {

    override suspend fun validate(validationUrl: String, domain: String): JsAny {
        val fullUrl = buildValidationUrl(validationUrl, domain)
        KPaymentLogger.tag(TAG).d("Merchant validation API URL: $fullUrl")
        return httpClient.fetchJson(fullUrl, "POST")
    }

    private fun buildValidationUrl(validationUrl: String, domain: String): String {
        val baseUrl = config.baseUrl
        val endpoint = config.merchantValidationEndpoint
        val encodedUrl = encodeURIComponent(validationUrl)
        val encodedDomain = encodeURIComponent(domain)
        val params = "url=$encodedUrl&domain=$encodedDomain"
        return "$baseUrl$endpoint?$params"
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("function(str) { return encodeURIComponent(str); }")
private external fun encodeURIComponent(str: String): String

@OptIn(ExperimentalWasmJsInterop::class)
internal class ApplePaySessionManager(
    private val config: ApplePayWebConfig,
    private val httpClient: ApplePayHttpClient = FetchApplePayHttpClient()
) {
    private val validationOrchestrator = ApplePayMerchantValidationOrchestrator(config, httpClient)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var validationJob: Job? = null

    fun setupSessionHandlers(
        session: ApplePaySessionInstance,
        onPaymentAuthorized: (String) -> Unit,
        onError: (ApplePayWebErrorCode, String?) -> Unit,
        onCancel: () -> Unit
    ) {
        session.onValidateMerchant = { event ->
            handleMerchantValidation(session, config.domain, event, onError)
        }

        session.onPaymentAuthorized = { event ->
            handlePaymentAuthorized(session, event, onPaymentAuthorized, onError)
        }

        session.onCancel = {
            KPaymentLogger.tag(TAG).d("Payment cancelled by user")
            onCancel()
        }
    }

    private fun handleMerchantValidation(
        session: ApplePaySessionInstance,
        domain: String,
        event: JsAny,
        onError: (ApplePayWebErrorCode, String?) -> Unit
    ) {
        runCatching {
            val rawValidationUrl = getValidationURL(event)
            KPaymentLogger.tag(TAG).d("Raw merchant validation URL: $rawValidationUrl")

            val decodedValidationUrl = decodeURIComponent(rawValidationUrl)
            KPaymentLogger.tag(TAG).d("Decoded merchant validation URL: $decodedValidationUrl")

            validationJob?.cancel()
            validationJob = scope.launch {
                runCatching {
                    val sessionData = validationOrchestrator.validate(decodedValidationUrl, domain)
                    KPaymentLogger.tag(TAG).d("Merchant validation succeeded: $sessionData")
                    session.completeMerchantValidation(sessionData)
                }.onFailure { error ->
                    KPaymentLogger.tag(TAG).e("Merchant validation failed", error)
                    session.abort()
                    onError(ApplePayWebErrorCode.MERCHANT_VALIDATION_FAILED, error.message)
                }
            }
        }.onFailure { ex ->
            KPaymentLogger.tag(TAG).d("Merchant validation failed: $ex")
            session.abort()
            onError(ApplePayWebErrorCode.MERCHANT_VALIDATION_FAILED, ex.message)
        }
    }

    fun cleanup() {
        validationJob?.cancel()
        validationJob = null
        scope.cancel()
    }

    private fun handlePaymentAuthorized(
        session: ApplePaySessionInstance,
        event: JsAny,
        onPaymentAuthorized: (String) -> Unit,
        onError: (ApplePayWebErrorCode, String?) -> Unit
    ) {
        runCatching {
            val tokenJs = getPaymentToken(event)
            val tokenJsonString = JSON.stringify(tokenJs.unsafeCast<JsAny>())
            KPaymentLogger.tag(TAG).d("Payment authorized successfully")
            session.completePayment(ApplePaySession.STATUS_SUCCESS)
            onPaymentAuthorized(tokenJsonString)
        }.onFailure { error ->
            KPaymentLogger.tag(TAG).e("Payment token retrieval failed", error)
            session.completePayment(ApplePaySession.STATUS_FAILURE)
            onError(ApplePayWebErrorCode.TOKEN_EXTRACTION_FAILED, error.message)
        }
    }
}
