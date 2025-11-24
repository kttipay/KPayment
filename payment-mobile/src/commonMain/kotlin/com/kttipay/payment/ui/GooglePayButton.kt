package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
expect fun GooglePayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    theme: GooglePayButtonTheme = GooglePayButtonTheme.Dark,
    type: GooglePayButtonType = GooglePayButtonType.Pay,
    cornerRadius: Dp = 4.dp,
    enabled: Boolean = true
)

