package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.kttipay.payment.api.PaymentLauncher
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult
import com.kttipay.payment.ui.launcher.rememberGooglePayLauncher

@Composable
actual fun PaymentButton(
    theme: NativePaymentTheme,
    type: NativePaymentType,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    radius: Dp
) {
    GooglePayButton(
        onClick = onClick,
        modifier = modifier,
        theme = theme.toGoogleTheme(),
        type = type.toGoogleType(),
        cornerRadius = radius,
        enabled = enabled
    )
}

@Composable
actual fun rememberNativePaymentLauncher(
    onResult: (PaymentResult) -> Unit
): PaymentLauncher = rememberGooglePayLauncher(onResult)

actual fun currentNativePaymentProvider(): PaymentProvider = PaymentProvider.GooglePay

private fun NativePaymentTheme.toGoogleTheme(): GooglePayButtonTheme = when (this) {
    NativePaymentTheme.Light,
    NativePaymentTheme.LightOutline -> GooglePayButtonTheme.Light
    NativePaymentTheme.Automatic,
    NativePaymentTheme.Dark -> GooglePayButtonTheme.Dark
}

private fun NativePaymentType.toGoogleType(): GooglePayButtonType = when (this) {
    NativePaymentType.Buy -> GooglePayButtonType.Buy
    NativePaymentType.Book -> GooglePayButtonType.Book
    NativePaymentType.Checkout -> GooglePayButtonType.Checkout
    NativePaymentType.Donate -> GooglePayButtonType.Donate
    NativePaymentType.Order -> GooglePayButtonType.Order
    NativePaymentType.Subscribe -> GooglePayButtonType.Subscribe
    NativePaymentType.Plain -> GooglePayButtonType.Plain
    NativePaymentType.Pay,
    NativePaymentType.Continue,
    NativePaymentType.AddMoney,
    NativePaymentType.TopUp -> GooglePayButtonType.Pay
}
