package com.kttipay.payment.internal.googlepay

import com.kttipay.payment.api.config.GooglePayWebConfig
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.unsafeCast

@OptIn(ExperimentalWasmJsInterop::class)
internal class GooglePayWebClientImpl(
    private val config: GooglePayWebConfig
) : GooglePayWebClient {

    private val paymentsClient by lazy { createPaymentsClient(config.googlePayEnvironment) }

    override fun checkAvailability(
        onSuccess: (Boolean) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val readyRequest = getIsReadyToPayRequest(config)

        paymentsClient
            .isReadyToPay(readyRequest)
            .then(
                onFulfilled = { data: JsAny ->
                    val response = data.unsafeCast<IsReadyToPayResponse>()
                    onSuccess(response.result)
                    null
                },
                onRejected = { error ->
                    onError(AvailabilityException("Availability check failed: $error"))
                    null
                }
            )
    }
}

class AvailabilityException(message: String) : Exception(message)
