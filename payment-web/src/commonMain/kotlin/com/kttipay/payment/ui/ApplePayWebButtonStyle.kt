package com.kttipay.payment.ui

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Visual style for the Apple Pay web button.
 *
 * Maps to the `buttonstyle` attribute of `<apple-pay-button>`.
 *
 * @see [Apple Pay Button Styling](https://developer.apple.com/documentation/applepayontheweb/styling-the-apple-pay-button-using-css)
 */
@Stable
sealed interface ApplePayWebButtonTheme {
    data object Black : ApplePayWebButtonTheme
    data object White : ApplePayWebButtonTheme
    data object WhiteOutline : ApplePayWebButtonTheme
}

/**
 * Button type / label for the Apple Pay web button.
 *
 * Maps to the `type` attribute of `<apple-pay-button>`.
 *
 * @see [Apple Pay Button Types](https://developer.apple.com/documentation/applepayontheweb/displaying_apple_pay_buttons_using_javascript)
 */
@Stable
sealed interface ApplePayWebButtonType {
    data object Buy : ApplePayWebButtonType
    data object Pay : ApplePayWebButtonType
    data object Plain : ApplePayWebButtonType
    data object Order : ApplePayWebButtonType
    data object Donate : ApplePayWebButtonType
    data object Continue : ApplePayWebButtonType
    data object CheckOut : ApplePayWebButtonType
    data object Book : ApplePayWebButtonType
    data object Subscribe : ApplePayWebButtonType
    data object AddMoney : ApplePayWebButtonType
    data object TopUp : ApplePayWebButtonType
    data object Reload : ApplePayWebButtonType
    data object Rent : ApplePayWebButtonType
    data object Support : ApplePayWebButtonType
    data object Tip : ApplePayWebButtonType
    data object SetUp : ApplePayWebButtonType
}

/**
 * Configuration for Apple Pay web button appearance.
 *
 * @param theme The button color theme (black, white, white-outline).
 * @param type The button label type (pay, buy, checkout, etc.).
 * @param cornerRadius Corner radius for the button.
 * @param locale BCP 47 locale for the button label (e.g., "en-US", "fr-FR").
 *               Defaults to null which uses the browser's locale.
 */
data class ApplePayWebButtonConfig(
    val theme: ApplePayWebButtonTheme = ApplePayWebButtonTheme.Black,
    val type: ApplePayWebButtonType = ApplePayWebButtonType.Pay,
    val cornerRadius: Dp = 4.dp,
    val locale: String? = null,
)
