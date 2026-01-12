package com.kttipay.payment.internal.googlepay

import com.kttipay.payment.api.config.GooglePayWebConfig
import com.kttipay.payment.internal.logging.KPaymentLogger
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsPromiseError
import kotlin.js.unsafeCast

internal interface GooglePayPaymentClient {
    fun requestPayment(
        amount: String,
        onResult: (GooglePayWebResult) -> Unit
    )
}

@OptIn(ExperimentalWasmJsInterop::class)
internal class GooglePayPaymentClientImpl(
    private val config: GooglePayWebConfig
) : GooglePayPaymentClient {

    private val paymentsClient by lazy { createPaymentsClient(config.googlePayEnvironment) }

    override fun requestPayment(
        amount: String,
        onResult: (GooglePayWebResult) -> Unit
    ) {
        KPaymentLogger.d("GooglePayPaymentClientImpl.requestPayment amount=$amount")
        val paymentRequest = loadPaymentDataRequestWithDefaults(amount, config = config)
        KPaymentLogger.d("GooglePayPaymentClientImpl.requestPayment request=$paymentRequest")

        paymentsClient
            .loadPaymentData(paymentRequest)
            .then(
                onFulfilled = { data: JsAny ->
                    runCatching {
                        KPaymentLogger.d("GooglePayPaymentClientImpl.requestPayment onFulfilled raw=$data")
                        val paymentData = data.unsafeCast<PaymentData>()
                        val tokenData = paymentData.paymentMethodData?.tokenizationData
                        val token = tokenData?.token

                        if (token.isNullOrEmpty()) {
                            throw IllegalStateException("Token missing or empty in payment data")
                        }

                        KPaymentLogger.d("GooglePayPaymentClientImpl.requestPayment token=$token")
                        onResult(GooglePayWebResult.Success(token))
                    }.onFailure { error ->
                        KPaymentLogger.e("GooglePayPaymentClientImpl.requestPayment token extraction failed", error)
                        onResult(
                            GooglePayWebResult.Error(
                                errorCode = GooglePayWebErrorCode.TOKEN_EXTRACTION_FAILED,
                                additionalMessage = error.message
                            )
                        )
                    }
                    null
                },
                onRejected = { error: JsPromiseError ->
                    KPaymentLogger.d("GooglePayPaymentClientImpl.requestPayment onRejected raw=$error")
                    val parsed = parsePaymentError(error)
                    KPaymentLogger.d("GooglePayPaymentClientImpl.requestPayment parsedError=${parsed::class.simpleName} message=${parsed.message}")

                    val result = when (parsed) {
                        is PaymentException.CancelledException -> GooglePayWebResult.Cancelled
                        is PaymentException.FailedException -> GooglePayWebResult.Error(
                            errorCode = GooglePayWebErrorCode.LOAD_PAYMENT_DATA_FAILED,
                            additionalMessage = parsed.message
                        )
                    }
                    onResult(result)
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
        statusCode.equals("CANCELED", ignoreCase = true) -> {
            PaymentException.CancelledException(statusMessage)
        }
        statusMessage.contains("AbortError", ignoreCase = true) -> {
            PaymentException.CancelledException(statusMessage)
        }
        statusMessage.contains("User closed", ignoreCase = true) -> {
            PaymentException.CancelledException(statusMessage)
        }
        statusMessage.contains("CANCELED", ignoreCase = true) -> {
            PaymentException.CancelledException(statusMessage)
        }
        else -> {
            PaymentException.FailedException(statusMessage)
        }
    }
}
