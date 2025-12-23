package com.kttipay.payment.ui.launcher

import androidx.compose.runtime.Composable
import com.kttipay.payment.api.PaymentLauncher
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult

@Composable
actual fun rememberGooglePayLauncher(onResult: (PaymentResult) -> Unit): PaymentLauncher {
    return object : PaymentLauncher {
        override val provider: PaymentProvider
            get() = PaymentProvider.GooglePay

        override fun launch(amount: String) {
            //No-op for ios
        }
    }
}
