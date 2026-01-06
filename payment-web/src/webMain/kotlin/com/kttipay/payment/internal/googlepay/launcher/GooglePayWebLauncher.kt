package com.kttipay.payment.internal.googlepay.launcher

import com.kttipay.payment.internal.googlepay.GooglePayPaymentClient
import com.kttipay.payment.internal.googlepay.GooglePayWebResultHandler
import com.kttipay.payment.internal.googlepay.PaymentException
import com.kttipay.payment.internal.logging.KPaymentLogger
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
internal class GooglePayWebLauncher(
    private val paymentClient: GooglePayPaymentClient,
    private val resultHandler: GooglePayWebResultHandler
) : IGooglePayWebLauncher {

    override fun launch(amount: String) {
        runCatching {
            executePaymentFlow(amount)
        }.onFailure { error ->
            KPaymentLogger.tag("GooglePayWebLauncher").d("launch error: $error")
            handlePaymentError(error)
        }
    }

    private fun executePaymentFlow(amount: String) {
        requestPaymentFromClient(amount)
    }

    private fun requestPaymentFromClient(amount: String) {
        paymentClient.requestPayment(
            amount = amount,
            onSuccess = { token ->
                resultHandler.onSuccess(token)
            },
            onError = { error ->
                KPaymentLogger.tag("GooglePayWebLauncher").d("requestPaymentFromClient: $error")
                handlePaymentError(error)
            }
        )
    }

    private fun handlePaymentError(error: Throwable) {
        KPaymentLogger.tag("GooglePayWebLauncher").d("handlePaymentError: $error")
        when (error) {
            is PaymentException.CancelledException -> resultHandler.onCancelled()
            else -> resultHandler.onError(error)
        }
    }
}
