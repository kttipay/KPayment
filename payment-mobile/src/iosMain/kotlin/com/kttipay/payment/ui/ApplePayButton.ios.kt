package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import platform.Foundation.NSSelectorFromString
import platform.PassKit.PKPaymentButton
import platform.PassKit.PKPaymentButtonStyle
import platform.PassKit.PKPaymentButtonStyleAutomatic
import platform.PassKit.PKPaymentButtonStyleBlack
import platform.PassKit.PKPaymentButtonStyleWhite
import platform.PassKit.PKPaymentButtonStyleWhiteOutline
import platform.PassKit.PKPaymentButtonType
import platform.PassKit.PKPaymentButtonTypeAddMoney
import platform.PassKit.PKPaymentButtonTypeBuy
import platform.PassKit.PKPaymentButtonTypeContinue
import platform.PassKit.PKPaymentButtonTypeInStore
import platform.PassKit.PKPaymentButtonTypePlain
import platform.PassKit.PKPaymentButtonTypeTopUp
import platform.UIKit.NSLayoutConstraint
import platform.UIKit.UIColor
import platform.UIKit.UIControlEventTouchUpInside
import platform.UIKit.UIScreen
import platform.UIKit.UIView
import platform.darwin.NSObject

@Composable
actual fun ApplePayButton(
    onClick: () -> Unit,
    modifier: Modifier,
    style: ApplePayButtonStyle,
    type: ApplePayButtonType,
    cornerRadius: Dp,
    enabled: Boolean,
    backgroundColor: Color,
) {
    ApplePayButton(
        modifier = modifier,
        type = type.toPKType(),
        style = style.toPKStyle(),
        radius = cornerRadius.toIosPoints(),
        backgroundColor = backgroundColor.toUIColor(),
        enabled = enabled,
        onClick = onClick
    )
}

@OptIn(ExperimentalForeignApi::class)
@Composable
fun ApplePayButton(
    modifier: Modifier = Modifier,
    type: PKPaymentButtonType,
    style: PKPaymentButtonStyle,
    radius: Double = 0.0,
    backgroundColor: UIColor,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val clickHandler = remember { MyClickHandler(onClick) }

    UIKitView(
        modifier = modifier,
        factory = {
            val container = UIView().apply {
                this.backgroundColor = backgroundColor
                layer.masksToBounds = true
                this.opaque = true
                clipsToBounds = true

            }

            val button = PKPaymentButton(paymentButtonType = type, paymentButtonStyle = style)
                .apply {
                    layer.cornerRadius = radius
                    clipsToBounds = true

                    this.enabled = enabled

                    addTarget(
                        target = clickHandler,
                        action = NSSelectorFromString("didTap"),
                        forControlEvents = UIControlEventTouchUpInside
                    )
                    translatesAutoresizingMaskIntoConstraints = false
                }

            container.addSubview(button)
            NSLayoutConstraint.activateConstraints(
                listOf(
                    button.topAnchor.constraintEqualToAnchor(container.topAnchor),
                    button.leadingAnchor.constraintEqualToAnchor(container.leadingAnchor),
                    button.trailingAnchor.constraintEqualToAnchor(container.trailingAnchor),
                    button.bottomAnchor.constraintEqualToAnchor(container.bottomAnchor),
                    button.heightAnchor.constraintGreaterThanOrEqualToConstant(44.0)
                )
            )

            container
        },
        update = { view ->
            view.apply {
                this.backgroundColor = backgroundColor
                (subviews.firstOrNull() as? PKPaymentButton)?.let { btn ->
                    btn.layer.cornerRadius = radius
                }
            }
        }
    )
}

private fun Color.toUIColor(): UIColor =
    UIColor(
        red = red.toDouble(),
        green = green.toDouble(),
        blue = blue.toDouble(),
        alpha = alpha.toDouble(),
    )

@Composable
private fun Dp.toIosPoints(): Double {
    val px = with(LocalDensity.current) { toPx() }
    val scale = UIScreen.mainScreen.scale
    return (px / scale)
}

private class MyClickHandler(private val onClick: () -> Unit) : NSObject() {
    @OptIn(BetaInteropApi::class)
    @ObjCAction
    fun didTap() {
        onClick()
    }
}

private fun ApplePayButtonStyle.toPKStyle(): PKPaymentButtonStyle = when (this) {
    is ApplePayButtonStyle.Black -> PKPaymentButtonStyleBlack
    is ApplePayButtonStyle.White -> PKPaymentButtonStyleWhite
    is ApplePayButtonStyle.WhiteOutline -> PKPaymentButtonStyleWhiteOutline
    is ApplePayButtonStyle.Auto -> PKPaymentButtonStyleAutomatic
}

private fun ApplePayButtonType.toPKType(): PKPaymentButtonType = when (this) {
    is ApplePayButtonType.Buy -> PKPaymentButtonTypeBuy
    is ApplePayButtonType.Plain -> PKPaymentButtonTypePlain
    is ApplePayButtonType.Pay -> PKPaymentButtonTypeInStore
    is ApplePayButtonType.Continue -> PKPaymentButtonTypeContinue
    is ApplePayButtonType.AddMoney -> PKPaymentButtonTypeAddMoney
    is ApplePayButtonType.TopUp -> PKPaymentButtonTypeTopUp
    else -> PKPaymentButtonTypePlain
}
