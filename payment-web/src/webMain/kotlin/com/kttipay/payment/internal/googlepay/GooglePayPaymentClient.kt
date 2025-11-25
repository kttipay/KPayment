package com.kttipay.payment.internal.googlepay

import com.kttipay.payment.internal.logging.KPaymentLogger
import com.kttipay.payment.api.config.GooglePayWebConfig
import org.kimplify.deci.Deci
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsPromiseError
import kotlin.js.unsafeCast

internal interface GooglePayPaymentClient {
    fun requestPayment(
        amount: Deci,
        onSuccess: (GooglePayToken) -> Unit,
        onError: (Throwable) -> Unit
    )
}

@OptIn(ExperimentalWasmJsInterop::class)
internal class GooglePayPaymentClientImpl(
    private val config: GooglePayWebConfig
) : GooglePayPaymentClient {

    private val paymentsClient by lazy { createPaymentsClient(config.googlePayEnvironment) }

    override fun requestPayment(
        amount: Deci,
        onSuccess: (GooglePayToken) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        KPaymentLogger.d("GooglePayPaymentClientImpl.requestPayment amount=$amount")
        val paymentRequest = loadPaymentDataRequestWithDefaults(amount.toString(), config = config)
        KPaymentLogger.d("GooglePayPaymentClientImpl.requestPayment request=$paymentRequest")

        paymentsClient
            .loadPaymentData(paymentRequest)
            .then(
                onFulfilled = { data: JsAny ->
                    KPaymentLogger.d("GooglePayPaymentClientImpl.requestPayment onFulfilled raw=$data")
                    val paymentData = data.unsafeCast<PaymentData>()
                    val token = paymentData.paymentMethodData.tokenizationData.token
                    KPaymentLogger.d("GooglePayPaymentClientImpl.requestPayment token=$token")
                    onSuccess(GooglePayToken(token))
                    null
                },
                onRejected = { error: JsPromiseError ->
                    KPaymentLogger.d("GooglePayPaymentClientImpl.requestPayment onRejected raw=$error")
                    val parsed = parsePaymentError(error)
                    KPaymentLogger.d("GooglePayPaymentClientImpl.requestPayment parsedError=${parsed::class.simpleName} message=${parsed.message}")
                    onError(parsed)
                    null
                }
            )
    }
}

data class GooglePayToken(val value: String)

sealed class PaymentException(message: String) : Exception(message) {
    class CancelledException(message: String) : PaymentException(message)
    class FailedException(message: String) : PaymentException(message)
}

@OptIn(ExperimentalWasmJsInterop::class)
private external interface GooglePayErrorLike : JsAny {
    val statusCode: String?
    val statusMessage: String?
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun parsePaymentError(error: JsPromiseError): PaymentException {
    val googlePayError = runCatching {
        error.unsafeCast<GooglePayErrorLike>()
    }.onFailure {
        KPaymentLogger.d("parsePaymentError unsafeCast failed: $it")
    }.getOrNull()

    val statusCode = googlePayError?.statusCode
    val statusMessage = googlePayError?.statusMessage.orEmpty()

    KPaymentLogger.tag("GooglePayPaymentClient").w("parsePaymentError extracted statusCode=$statusCode statusMessage=$statusMessage")

    return when {
        statusCode.equals("CANCELED", ignoreCase = true) ->
            PaymentException.CancelledException(statusMessage)
        statusMessage.contains("AbortError", ignoreCase = true) ->
            PaymentException.CancelledException(statusMessage)
        statusMessage.contains("User closed", ignoreCase = true) ->
            PaymentException.CancelledException(statusMessage)
        statusMessage.contains("CANCELED", ignoreCase = true) ->
            PaymentException.CancelledException(statusMessage)
        else ->
            PaymentException.FailedException(statusMessage)
    }
}