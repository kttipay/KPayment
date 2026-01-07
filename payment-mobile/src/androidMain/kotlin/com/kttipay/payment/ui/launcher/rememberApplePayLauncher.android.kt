package com.kttipay.payment.ui.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kttipay.payment.api.PaymentErrorReason
import com.kttipay.payment.api.PaymentLauncher
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
actual fun rememberApplePayLauncher(
    onResult: (PaymentResult) -> Unit
): PaymentLauncher = remember {
    object : PaymentLauncher {
        override val provider: PaymentProvider = PaymentProvider.ApplePay
        override val isProcessing: StateFlow<Boolean> = MutableStateFlow(false)

        override fun launch(amount: String) {
            onResult(
                PaymentResult.Error(
                    provider = provider,
                    reason = PaymentErrorReason.NotAvailable,
                    message = "Apple Pay is not supported on Android"
                )
            )
        }
    }
}
