package com.kttipay.payment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val GoogleBlue = Color(0xFF4285F4)
private val GoogleRed = Color(0xFFEA4335)
private val GoogleYellow = Color(0xFFFBBC05)
private val GoogleGreen = Color(0xFF34A853)

private val previewRadius = 24.dp

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
    val actionPrefix = when (type) {
        NativePaymentType.Buy -> "Buy with "
        NativePaymentType.Book -> "Book with "
        NativePaymentType.Checkout -> "Checkout with "
        NativePaymentType.Donate -> "Donate with "
        NativePaymentType.Order -> "Order with "
        NativePaymentType.Subscribe -> "Subscribe with "
        NativePaymentType.Plain -> null
        NativePaymentType.Pay,
        NativePaymentType.Continue,
        NativePaymentType.AddMoney,
        NativePaymentType.TopUp -> "Pay with "
    }
    val showOutline = theme == NativePaymentTheme.LightOutline
    val alpha = if (enabled) 1f else 0.38f

    PaymentButtonShell(
        backgroundColor = backgroundColor,
        showOutline = showOutline,
        enabled = enabled,
        onClick = onClick,
        modifier = modifier,
        radius = radius
    ) {
        if (actionPrefix != null) {
            Text(
                text = actionPrefix,
                color = contentColor.copy(alpha = alpha),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        GooglePayMark(alpha = alpha)
    }
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
    val actionPrefix = when (type) {
        NativePaymentType.AddMoney -> "Add Money with "
        NativePaymentType.Buy -> "Buy with "
        NativePaymentType.Continue -> "Continue with "
        NativePaymentType.Pay -> "Pay with "
        NativePaymentType.TopUp -> "Top Up with "
        NativePaymentType.Plain -> null
        NativePaymentType.Book,
        NativePaymentType.Checkout,
        NativePaymentType.Donate,
        NativePaymentType.Order,
        NativePaymentType.Subscribe -> "Pay with "
    }
    val showOutline = theme == NativePaymentTheme.LightOutline
    val alpha = if (enabled) 1f else 0.38f

    PaymentButtonShell(
        backgroundColor = backgroundColor,
        showOutline = showOutline,
        enabled = enabled,
        onClick = onClick,
        modifier = modifier,
        radius = radius
    ) {
        if (actionPrefix != null) {
            Text(
                text = actionPrefix,
                color = contentColor.copy(alpha = alpha),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        ApplePayMark(contentColor = contentColor, alpha = alpha)
    }
}

@Composable
private fun GooglePayMark(alpha: Float) {
    val brandText = buildAnnotatedString {
        withStyle(SpanStyle(color = GoogleBlue.copy(alpha = alpha))) { append("G") }
        withStyle(SpanStyle(color = GoogleRed.copy(alpha = alpha))) { append("o") }
        withStyle(SpanStyle(color = GoogleYellow.copy(alpha = alpha))) { append("o") }
        withStyle(SpanStyle(color = GoogleBlue.copy(alpha = alpha))) { append("g") }
        withStyle(SpanStyle(color = GoogleGreen.copy(alpha = alpha))) { append("l") }
        withStyle(SpanStyle(color = GoogleRed.copy(alpha = alpha))) { append("e") }
    }
    Text(
        text = brandText,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
    )
    Spacer(modifier = Modifier.width(3.dp))
    Text(
        text = "Pay",
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.Gray.copy(alpha = alpha)
    )
}

@Composable
private fun ApplePayMark(contentColor: Color, alpha: Float) {
    val applePayText = buildAnnotatedString {
        withStyle(SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.Light)) {
            append("\uD83C\uDF4E") // 🍎
        }
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, letterSpacing = (-0.5).sp)) {
            append(" Pay")
        }
    }
    Text(
        text = applePayText,
        color = contentColor.copy(alpha = alpha),
        fontSize = 17.sp,
    )
}

@Composable
private fun PaymentButtonShell(
    backgroundColor: Color,
    showOutline: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    radius: Dp,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(radius)
    val alpha = if (enabled) 1f else 0.38f
    val borderModifier = if (showOutline) {
        Modifier.border(1.dp, Color.LightGray.copy(alpha = alpha), shape)
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .then(borderModifier)
            .background(backgroundColor.copy(alpha = if (enabled) 1f else 0.6f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            content()
        }
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
            modifier = Modifier.padding(horizontal = 8.dp),
            radius = previewRadius
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
            modifier = Modifier.padding(horizontal = 8.dp),
            radius = previewRadius
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
            modifier = Modifier.padding(horizontal = 8.dp),
            radius = previewRadius
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
            modifier = Modifier.padding(horizontal = 8.dp),
            radius = previewRadius
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
            modifier = Modifier.padding(horizontal = 8.dp),
            radius = previewRadius
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
            modifier = Modifier.padding(horizontal = 8.dp),
            radius = 28.dp
        )
    }
}
