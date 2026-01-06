package com.kttipay.payment.ui

import androidx.compose.runtime.Stable

@Stable
sealed interface ApplePayButtonType {
    data object AddMoney : ApplePayButtonType

    data object Buy : ApplePayButtonType

    data object Continue : ApplePayButtonType

    data object Pay : ApplePayButtonType

    data object Plain : ApplePayButtonType

    data object TopUp : ApplePayButtonType

    /** Setup payments */
    data object SetUp : ApplePayButtonType
}

@Stable
sealed interface ApplePayButtonStyle {
    data object Black : ApplePayButtonStyle

    data object White : ApplePayButtonStyle

    data object WhiteOutline : ApplePayButtonStyle

    data object Auto : ApplePayButtonStyle
}
