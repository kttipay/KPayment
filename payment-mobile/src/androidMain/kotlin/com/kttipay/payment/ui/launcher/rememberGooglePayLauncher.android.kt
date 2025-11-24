package com.kttipay.payment.ui.launcher

import androidx.compose.runtime.Composable
import com.kttipay.payment.api.PaymentLauncher
import com.kttipay.payment.api.PaymentResult
import com.kttipay.payment.internal.googlepay.rememberGooglePayLauncher

@Composable
actual fun rememberGooglePayLauncher(onResult: (PaymentResult) -> Unit): PaymentLauncher =
    rememberGooglePayLauncher(onResult)
