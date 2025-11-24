package com.kttipay.payment.ui.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kttipay.payment.api.PaymentErrorReason
import com.kttipay.payment.api.PaymentLauncher
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult
import org.kimplify.deci.Deci

@Composable
actual fun rememberApplePayLauncher(
    onResult: (PaymentResult) -> Unit
): PaymentLauncher = remember {
    object : PaymentLauncher {
        override val provider: PaymentProvider = PaymentProvider.ApplePay

        override fun launch(amount: Deci) {
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
