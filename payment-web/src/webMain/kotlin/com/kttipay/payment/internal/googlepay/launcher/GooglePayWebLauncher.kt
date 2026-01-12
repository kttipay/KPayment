package com.kttipay.payment.internal.googlepay.launcher

import com.kttipay.payment.api.PaymentErrorReason
import com.kttipay.payment.api.PaymentLauncher
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult
import com.kttipay.payment.internal.googlepay.GooglePayPaymentClient
import com.kttipay.payment.internal.googlepay.toPaymentResult
import com.kttipay.payment.internal.logging.KPaymentLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
internal class GooglePayWebLauncher(
    private val paymentClient: GooglePayPaymentClient,
    private val onResult: (PaymentResult) -> Unit
) : PaymentLauncher {

    override val provider: PaymentProvider = PaymentProvider.GooglePay

    private val _isProcessing = MutableStateFlow(false)
    override val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

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

        KPaymentLogger.tag("GooglePayWebLauncher").d("Launching Google Pay with amount: $amount")
        paymentClient.requestPayment(amount = amount) { result ->
            _isProcessing.value = false
            onResult(result.toPaymentResult())
        }
    }
}
