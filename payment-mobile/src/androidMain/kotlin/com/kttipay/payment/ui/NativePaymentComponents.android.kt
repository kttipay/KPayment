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
private fun GooglePayButtonPreviewStub(
    theme: NativePaymentTheme,
    type: NativePaymentType,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    radius: Dp
) {
    val backgroundColor = when (theme) {
        NativePaymentTheme.Dark, NativePaymentTheme.Automatic -> Color.Black
        NativePaymentTheme.Light, NativePaymentTheme.LightOutline -> Color.White
    }
    val contentColor = when (theme) {
        NativePaymentTheme.Dark, NativePaymentTheme.Automatic -> Color.White
        NativePaymentTheme.Light, NativePaymentTheme.LightOutline -> Color.Black
    }
    val label = when (type) {
        NativePaymentType.Buy -> "Buy with Google Pay"
        NativePaymentType.Book -> "Book with Google Pay"
        NativePaymentType.Checkout -> "Checkout with Google Pay"
        NativePaymentType.Donate -> "Donate with Google Pay"
        NativePaymentType.Order -> "Order with Google Pay"
        NativePaymentType.Subscribe -> "Subscribe with Google Pay"
        NativePaymentType.Plain -> "Google Pay"
        NativePaymentType.Pay,
        NativePaymentType.Continue,
        NativePaymentType.AddMoney,
        NativePaymentType.TopUp -> "Pay with Google Pay"
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
