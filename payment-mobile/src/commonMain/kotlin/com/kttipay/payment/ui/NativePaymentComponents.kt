package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.kttipay.payment.api.PaymentLauncher
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult

/**
 * Platform agnostic theme definition that maps to native Google Pay / Apple Pay styles.
 */
enum class NativePaymentTheme {
    Dark,
    Light,
    LightOutline,
    Automatic
}

/**
 * Platform agnostic button types mirroring the options from both Google Pay and Apple Pay.
 */
enum class NativePaymentType {
    Pay,
    Buy,
    Book,
    Checkout,
    Donate,
    Order,
    Subscribe,
    Plain,
    Continue,
    AddMoney,
    TopUp
}

@Composable
expect fun PaymentButton(
    theme: NativePaymentTheme,
    type: NativePaymentType,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    radius: Dp
)

@Composable
expect fun rememberNativePaymentLauncher(
    onResult: (PaymentResult) -> Unit
): PaymentLauncher

expect fun currentNativePaymentProvider(): PaymentProvider
