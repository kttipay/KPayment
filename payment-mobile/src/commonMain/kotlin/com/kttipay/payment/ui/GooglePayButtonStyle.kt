package com.kttipay.payment.ui

import androidx.compose.runtime.Stable

@Stable
enum class GooglePayButtonTheme {
    Dark,
    Light
}

@Stable
enum class GooglePayButtonType {
    Buy,
    Book,
    Checkout,
    Donate,
    Order,
    Pay,
    Subscribe,
    Plain
}
