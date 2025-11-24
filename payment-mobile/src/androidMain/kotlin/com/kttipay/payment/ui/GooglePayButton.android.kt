package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.google.android.gms.wallet.button.ButtonConstants
import com.google.pay.button.ButtonTheme
import com.google.pay.button.ButtonType
import com.google.pay.button.PayButton
import com.kttipay.payment.internal.googlepay.GooglePayEnvironment

@Composable
actual fun GooglePayButton(
    onClick: () -> Unit,
    modifier: Modifier,
    theme: GooglePayButtonTheme,
    type: GooglePayButtonType,
    cornerRadius: Dp,
    enabled: Boolean
) {
    PayButton(
        modifier = modifier,
        onClick = onClick,
        theme = theme.toButtonTheme(),
        type = type.toButtonType(),
        enabled = enabled,
        allowedPaymentMethods = GooglePayEnvironment.allowedPaymentMethodsJson(),
    )
}

private fun GooglePayButtonType.toButtonType(): ButtonType {
    return when(this) {
        GooglePayButtonType.Buy -> ButtonType.Buy
        GooglePayButtonType.Book -> ButtonType.Book
        GooglePayButtonType.Checkout -> ButtonType.Checkout
        GooglePayButtonType.Donate -> ButtonType.Donate
        GooglePayButtonType.Order -> ButtonType.Order
        GooglePayButtonType.Pay -> ButtonType.Pay
        GooglePayButtonType.Subscribe -> ButtonType.Subscribe
        GooglePayButtonType.Plain -> ButtonType.Plain
    }
}

private fun GooglePayButtonTheme.toButtonTheme(): ButtonTheme {
    return when(this) {
        GooglePayButtonTheme.Dark -> ButtonTheme.Dark
        GooglePayButtonTheme.Light -> ButtonTheme.Light
    }
}

//enum class ButtonType(val value: Int) {
//    Book(ButtonConstants.ButtonType.BOOK),
//    Buy(ButtonConstants.ButtonType.BUY),
//    Checkout(ButtonConstants.ButtonType.CHECKOUT),
//    Donate(ButtonConstants.ButtonType.DONATE),
//    Order(ButtonConstants.ButtonType.ORDER),
//    Pay(ButtonConstants.ButtonType.PAY),
//    Plain(ButtonConstants.ButtonType.PLAIN),
//    Subscribe(ButtonConstants.ButtonType.SUBSCRIBE),
//}
