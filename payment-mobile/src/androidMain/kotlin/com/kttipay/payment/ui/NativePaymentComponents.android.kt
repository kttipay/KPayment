package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Dp
import com.kttipay.payment.api.PaymentErrorReason
import com.kttipay.payment.api.PaymentLauncher
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult
import com.kttipay.payment.ui.launcher.rememberGooglePayLauncher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
actual fun PaymentButton(
    theme: NativePaymentTheme,
    type: NativePaymentType,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    radius: Dp
) {
    if (LocalInspectionMode.current) {
        GooglePayButtonPreviewStub(
            theme = theme,
            type = type,
            enabled = enabled,
            onClick = onClick,
            modifier = modifier,
            radius = radius
        )
    } else {
        GooglePayButton(
            onClick = onClick,
            modifier = modifier,
            theme = theme.toGoogleTheme(),
            type = type.toGoogleType(),
            cornerRadius = radius,
            enabled = enabled
        )
    }
}

@Composable
actual fun rememberNativePaymentLauncher(
    onResult: (PaymentResult) -> Unit
): PaymentLauncher {
    val manager = LocalMobilePaymentManager.current
    return if (manager.config.googlePay != null) {
        rememberGooglePayLauncher(onResult)
    } else {
        NotConfiguredLauncher(onResult)
    }
}

actual fun currentNativePaymentProvider(): PaymentProvider = PaymentProvider.GooglePay

private class NotConfiguredLauncher(
    private val onResult: (PaymentResult) -> Unit
) : PaymentLauncher {
    override val provider: PaymentProvider = PaymentProvider.GooglePay
    override val isProcessing: StateFlow<Boolean> = MutableStateFlow(false)

    override fun launch(amount: String) {
        onResult(
            PaymentResult.Error(
                provider = provider,
                reason = PaymentErrorReason.NotAvailable,
                message = "Google Pay is not configured"
            )
        )
    }
}

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
