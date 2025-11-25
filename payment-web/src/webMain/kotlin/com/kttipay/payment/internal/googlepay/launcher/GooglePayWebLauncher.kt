package com.kttipay.payment.internal.googlepay.launcher

import org.kimplify.cedar.logging.Cedar
import com.kttipay.common.deci.Deci
import com.kttipay.payment.internal.googlepay.GooglePayPaymentClient
import com.kttipay.payment.internal.googlepay.GooglePayWebResultHandler
import com.kttipay.payment.internal.googlepay.PaymentException
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
internal class GooglePayWebLauncher(
    private val paymentClient: GooglePayPaymentClient,
    private val resultHandler: GooglePayWebResultHandler
) : IGooglePayWebLauncher {

    override fun launch(amount: Deci) {
        runCatching {
            executePaymentFlow(amount)
        }.onFailure { error ->
            Cedar.tag("GooglePayWebLauncher").d("launch error: $error")
            handlePaymentError(error)
        }
    }

    private fun executePaymentFlow(amount: Deci) {
        requestPaymentFromClient(amount)
    }

    private fun requestPaymentFromClient(amount: Deci) {
        paymentClient.requestPayment(
            amount = amount,
            onSuccess = { token ->
                resultHandler.onSuccess(token)
            },
            onError = { error ->
                Cedar.tag("GooglePayWebLauncher").d("requestPaymentFromClient: $error")
                handlePaymentError(error)
            }
        )
    }

    private fun handlePaymentError(error: Throwable) {
        Cedar.tag("GooglePayWebLauncher").d("handlePaymentError: $error")
        when (error) {
            is PaymentException.CancelledException -> resultHandler.onCancelled()
            else -> resultHandler.onError(error)
        }
    }
}
