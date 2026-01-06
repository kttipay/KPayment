package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
actual fun ApplePayButton(
    onClick: () -> Unit,
    modifier: Modifier,
    style: ApplePayButtonStyle,
    type: ApplePayButtonType,
    cornerRadius: Dp,
    enabled: Boolean,
) {
    // No-op
}
