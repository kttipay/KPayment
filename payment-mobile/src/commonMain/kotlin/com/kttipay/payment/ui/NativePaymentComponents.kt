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
 *
 * Platform mappings:
 * - **Android (Google Pay):**
 *   - [Dark], [Automatic] → Dark theme
 *   - [Light], [LightOutline] → Light theme
 * - **iOS (Apple Pay):**
 *   - [Dark] → Black style
 *   - [Light] → White style
 *   - [LightOutline] → White with outline
 *   - [Automatic] → Automatic (adapts to system theme)
 */
enum class NativePaymentTheme {
    Dark,
    Light,
    LightOutline,
    Automatic
}

/**
 * Platform agnostic button types mirroring the options from both Google Pay and Apple Pay.
 *
 * Platform mappings:
 * - **Android (Google Pay):**
 *   - [Buy] → "Buy with Google Pay"
 *   - [Book] → "Book with Google Pay"
 *   - [Checkout] → "Checkout with Google Pay"
 *   - [Donate] → "Donate with Google Pay"
 *   - [Order] → "Order with Google Pay"
 *   - [Subscribe] → "Subscribe with Google Pay"
 *   - [Plain] → Google Pay logo only
 *   - [Pay], [Continue], [AddMoney], [TopUp] → "Pay" (default)
 *
 * - **iOS (Apple Pay):**
 *   - [AddMoney] → "Add Money"
 *   - [Buy] → "Buy with Apple Pay"
 *   - [Continue] → "Continue with Apple Pay"
 *   - [Pay] → "Pay with Apple Pay"
 *   - [TopUp] → "Top Up"
 *   - [Plain] → Apple Pay logo only
 *   - [Book], [Checkout], [Donate], [Order], [Subscribe] → "Pay" (fallback to default)
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
