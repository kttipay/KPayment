package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
actual fun GooglePayButton(
    onClick: () -> Unit,
    modifier: Modifier,
    theme: GooglePayButtonTheme,
    type: GooglePayButtonType,
    cornerRadius: Dp,
    enabled: Boolean
) {
    //No-op for ios
}