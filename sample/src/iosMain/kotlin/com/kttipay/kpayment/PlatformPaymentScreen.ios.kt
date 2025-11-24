package com.kttipay.kpayment

import androidx.compose.runtime.Composable
import com.kttipay.kpayment.screens.PaymentScreen
import com.kttipay.payment.api.PaymentProvider

/**
 * iOS implementation of the platform payment screen.
 * Shows Apple Pay integration demo using the unified PaymentScreen.
 */
@Composable
actual fun PlatformPaymentScreen() {
    PaymentScreen(provider = PaymentProvider.ApplePay)
}
