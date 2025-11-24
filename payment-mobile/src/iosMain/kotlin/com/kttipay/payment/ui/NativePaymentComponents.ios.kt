package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.kttipay.payment.api.PaymentErrorReason
import com.kttipay.payment.api.PaymentLauncher
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult
import com.kttipay.payment.ui.launcher.rememberApplePayLauncher
import org.kimplify.deci.Deci

@Composable
actual fun PaymentButton(
    theme: NativePaymentTheme,
    type: NativePaymentType,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    backgroundColor: Color,
    radius: Dp
) {
    ApplePayButton(
        onClick = onClick,
        modifier = modifier,
        style = theme.toAppleStyle(),
        type = type.toAppleType(),
        cornerRadius = radius,
        enabled = enabled,
        backgroundColor = backgroundColor
    )
}

@Composable
actual fun rememberNativePaymentLauncher(
    onResult: (PaymentResult) -> Unit
): PaymentLauncher {
    val paymentManager = LocalMobilePaymentManager.current
    return if (paymentManager.applePayConfig() != null) {
        rememberApplePayLauncher(onResult)
    } else {
        NotConfiguredLauncher(onResult)
    }
}

actual fun currentNativePaymentProvider(): PaymentProvider = PaymentProvider.ApplePay

private class NotConfiguredLauncher(
    private val onResult: (PaymentResult) -> Unit
) : PaymentLauncher {
    override val provider: PaymentProvider = PaymentProvider.ApplePay

    override fun launch(amount: Deci) {
        onResult(
            PaymentResult.Error(
                provider = provider,
                reason = PaymentErrorReason.NotAvailable,
                message = "Apple Pay is not configured"
            )
        )
    }
}

private fun NativePaymentTheme.toAppleStyle(): ApplePayButtonStyle = when (this) {
    NativePaymentTheme.Dark -> ApplePayButtonStyle.Black
    NativePaymentTheme.Light -> ApplePayButtonStyle.White
    NativePaymentTheme.LightOutline -> ApplePayButtonStyle.WhiteOutline
    NativePaymentTheme.Automatic -> ApplePayButtonStyle.Auto
}

private fun NativePaymentType.toAppleType(): ApplePayButtonType = when (this) {
    NativePaymentType.AddMoney -> ApplePayButtonType.AddMoney
    NativePaymentType.Buy -> ApplePayButtonType.Buy
    NativePaymentType.Continue -> ApplePayButtonType.Continue
    NativePaymentType.Pay -> ApplePayButtonType.Pay
    NativePaymentType.TopUp -> ApplePayButtonType.TopUp
    NativePaymentType.Plain -> ApplePayButtonType.Plain
    // Map unsupported Google specific types to closest Apple equivalent
    NativePaymentType.Book,
    NativePaymentType.Checkout,
    NativePaymentType.Donate,
    NativePaymentType.Order,
    NativePaymentType.Subscribe -> ApplePayButtonType.Pay
}
