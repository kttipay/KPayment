package com.kttipay.payment.ui.launcher

import androidx.compose.runtime.Composable
import com.kttipay.payment.api.PaymentLauncher
import com.kttipay.payment.api.PaymentResult

@Composable
expect fun rememberApplePayLauncher(
    onResult: (PaymentResult) -> Unit
): PaymentLauncher
