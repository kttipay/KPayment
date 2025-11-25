package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
expect fun ApplePayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ApplePayButtonStyle,
    type: ApplePayButtonType,
    cornerRadius: Dp = 4.dp,
    enabled: Boolean = true,
)