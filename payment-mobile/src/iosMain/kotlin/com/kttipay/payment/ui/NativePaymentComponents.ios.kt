package com.kttipay.payment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kttipay.payment.api.PaymentErrorReason
import com.kttipay.payment.api.PaymentLauncher
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult
import com.kttipay.payment.ui.launcher.rememberApplePayLauncher
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
        ApplePayButtonPreviewStub(
            theme = theme,
            type = type,
            enabled = enabled,
            onClick = onClick,
            modifier = modifier,
            radius = radius
        )
    } else {
        ApplePayButton(
            onClick = onClick,
            modifier = modifier,
            style = theme.toAppleStyle(),
            type = type.toAppleType(),
            cornerRadius = radius,
            enabled = enabled,
        )
    }
}

@Composable
private fun ApplePayButtonPreviewStub(
    theme: NativePaymentTheme,
    type: NativePaymentType,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    radius: Dp
) {
    val backgroundColor = when (theme) {
        NativePaymentTheme.Dark -> Color.Black
        NativePaymentTheme.Light -> Color.White
        NativePaymentTheme.LightOutline -> Color.White
        NativePaymentTheme.Automatic -> Color.Black
    }
    val contentColor = when (theme) {
        NativePaymentTheme.Dark, NativePaymentTheme.Automatic -> Color.White
        NativePaymentTheme.Light, NativePaymentTheme.LightOutline -> Color.Black
    }
    val label = when (type) {
        NativePaymentType.AddMoney -> "Add Money with Apple Pay"
        NativePaymentType.Buy -> "Buy with  Pay"
        NativePaymentType.Continue -> "Continue with Apple Pay"
        NativePaymentType.Pay -> "Pay with Apple Pay"
        NativePaymentType.TopUp -> "Top Up with Apple Pay"
        NativePaymentType.Plain -> "Apple Pay"
        NativePaymentType.Book,
        NativePaymentType.Checkout,
        NativePaymentType.Donate,
        NativePaymentType.Order,
        NativePaymentType.Subscribe -> "Pay with Apple Pay"
    }

    val shape = RoundedCornerShape(radius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor.copy(alpha = if (enabled) 1f else 0.5f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = contentColor.copy(alpha = if (enabled) 1f else 0.5f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
actual fun rememberNativePaymentLauncher(
    onResult: (PaymentResult) -> Unit
): PaymentLauncher {
    val manager = LocalMobilePaymentManager.current
    return if (manager.config.applePayMobile != null) {
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
    override val isProcessing: StateFlow<Boolean> = MutableStateFlow(false)

    override fun launch(amount: String) {
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
