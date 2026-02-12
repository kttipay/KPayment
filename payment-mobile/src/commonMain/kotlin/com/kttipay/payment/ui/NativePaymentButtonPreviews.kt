package com.kttipay.payment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun GooglePayButtonPreviewStub(
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

    PaymentButtonStub(
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        label = label,
        enabled = enabled,
        onClick = onClick,
        modifier = modifier,
        radius = radius
    )
}

@Composable
internal fun ApplePayButtonPreviewStub(
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
        NativePaymentType.AddMoney -> "Add Money with Apple Pay"
        NativePaymentType.Buy -> "Buy with Apple Pay"
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

    PaymentButtonStub(
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        label = label,
        enabled = enabled,
        onClick = onClick,
        modifier = modifier,
        radius = radius
    )
}

@Composable
private fun PaymentButtonStub(
    backgroundColor: Color,
    contentColor: Color,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    radius: Dp
) {
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

@Preview
@Composable
private fun GooglePayButtonDarkPreview() {
    MaterialTheme {
        GooglePayButtonPreviewStub(
            theme = NativePaymentTheme.Dark,
            type = NativePaymentType.Pay,
            enabled = true,
            onClick = {},
            modifier = Modifier,
            radius = 8.dp
        )
    }
}

@Preview
@Composable
private fun GooglePayButtonLightPreview() {
    MaterialTheme {
        GooglePayButtonPreviewStub(
            theme = NativePaymentTheme.Light,
            type = NativePaymentType.Buy,
            enabled = true,
            onClick = {},
            modifier = Modifier,
            radius = 8.dp
        )
    }
}

@Preview
@Composable
private fun GooglePayButtonDisabledPreview() {
    MaterialTheme {
        GooglePayButtonPreviewStub(
            theme = NativePaymentTheme.Dark,
            type = NativePaymentType.Pay,
            enabled = false,
            onClick = {},
            modifier = Modifier,
            radius = 8.dp
        )
    }
}

@Preview
@Composable
private fun ApplePayButtonDarkPreview() {
    MaterialTheme {
        ApplePayButtonPreviewStub(
            theme = NativePaymentTheme.Dark,
            type = NativePaymentType.Pay,
            enabled = true,
            onClick = {},
            modifier = Modifier,
            radius = 8.dp
        )
    }

}

@Preview
@Composable
private fun ApplePayButtonLightPreview() {
    MaterialTheme {
        ApplePayButtonPreviewStub(
            theme = NativePaymentTheme.Light,
            type = NativePaymentType.Buy,
            enabled = true,
            onClick = {},
            modifier = Modifier,
            radius = 8.dp
        )
    }

}

@Preview
@Composable
private fun ApplePayButtonDisabledPreview() {
    MaterialTheme {
        ApplePayButtonPreviewStub(
            theme = NativePaymentTheme.Dark,
            type = NativePaymentType.Pay,
            enabled = false,
            onClick = {},
            modifier = Modifier,
            radius = 8.dp
        )
    }
}
