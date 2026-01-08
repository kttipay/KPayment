package com.kttipay.payment.ui.local

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.pay.button.ButtonTheme
import com.google.pay.button.ButtonType
import com.google.pay.button.PayButton

@Composable
fun GooglePayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    allowedPaymentMethods: String,
    theme: ButtonTheme = ButtonTheme.Dark,
    type: ButtonType = ButtonType.Pay,
    enabled: Boolean = true
) {
    PayButton(
        modifier = modifier,
        onClick = onClick,
        theme = theme,
        type = type,
        enabled = enabled,
        allowedPaymentMethods = allowedPaymentMethods
    )
}
