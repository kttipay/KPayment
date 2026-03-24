@file:OptIn(ExperimentalWasmJsInterop::class)
@file:Suppress("OPT_IN_USAGE")

package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.HtmlElementView
import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import com.kttipay.payment.internal.applepay.isApplePaySdkLoaded
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.unsafeCast

/**
 * Renders a native `<apple-pay-button>` HTML element from the Apple Pay JS SDK
 * within the Compose layout using [HtmlElementView].
 *
 * This is one of two ways to trigger Apple Pay in the library:
 *
 * **Option 1 — This composable:** Renders the official Apple-branded `<apple-pay-button>`
 * custom element. The SDK handles the button appearance and QR code flow automatically.
 *
 * **Option 2 — Custom button with `rememberApplePayWebLauncher`:** Use any Compose button
 * and call `launcher.launch(amount)` on click. The SDK polyfills `ApplePaySession` for
 * non-Safari browsers, so `session.begin()` triggers the QR code flow automatically.
 * This gives you full control over button styling.
 *
 * **Critical:** The [onClick] callback is invoked synchronously from the DOM
 * click event. The consumer MUST call `launcher.launch(amount)` synchronously
 * within this callback — no `launch {}` or `delay`. Any async hop breaks the
 * browser's user gesture activation token and the payment flow will fail.
 *
 * @param onClick Called synchronously on button click. Launch the Apple Pay session here.
 * @param modifier Compose modifier for sizing/layout.
 * @param config Button appearance configuration (theme, type, corner radius, locale).
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ApplePayWebButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    config: ApplePayWebButtonConfig = ApplePayWebButtonConfig(),
) {
    val sdkAvailable = runCatching { isApplePaySdkLoaded() }.getOrDefault(false)
    if (!sdkAvailable) return

    val theme = config.theme.toAttributeValue()
    val type = config.type.toAttributeValue()
    val locale = config.locale
    val radiusPx = config.cornerRadius.value.toInt()

    HtmlElementView(
        factory = {
            document.createElement("apple-pay-button").unsafeCast<HTMLElement>().apply {
                setAttribute("buttonstyle", theme)
                setAttribute("type", type)
                if (locale != null) setAttribute("locale", locale)
                applyButtonCss(radiusPx)
                addEventListener("click", { onClick() })
            }
        },
        modifier = modifier,
        update = { el ->
            val parent = el.parentElement
            if (parent != null) {
                val w = parent.clientWidth
                val h = parent.clientHeight
                if (w > 0) el.style.setProperty("--apple-pay-button-width", "${w}px")
                if (h > 0) el.style.setProperty("--apple-pay-button-height", "${h}px")
            }
            el.setAttribute("buttonstyle", theme)
            el.setAttribute("type", type)
            if (locale != null) el.setAttribute("locale", locale)
            applyButtonCss(el, radiusPx)
        },
        onRelease = {},
        onReset = null
    )
}

private fun HTMLElement.applyButtonCss(radiusPx: Int) {
    applyButtonCss(this, radiusPx)
}

private fun applyButtonCss(el: HTMLElement, radiusPx: Int) {
    el.style.setProperty("--apple-pay-button-border-radius", "${radiusPx}px")
    el.style.setProperty("--apple-pay-button-padding", "0px 0px")
    el.style.setProperty("--apple-pay-button-box-sizing", "border-box")
    el.style.display = "block"
}

internal fun ApplePayWebButtonTheme.toAttributeValue(): String = when (this) {
    ApplePayWebButtonTheme.Black -> "black"
    ApplePayWebButtonTheme.White -> "white"
    ApplePayWebButtonTheme.WhiteOutline -> "white-outline"
}

internal fun ApplePayWebButtonType.toAttributeValue(): String = when (this) {
    ApplePayWebButtonType.Buy -> "buy"
    ApplePayWebButtonType.Pay -> "pay"
    ApplePayWebButtonType.Plain -> "plain"
    ApplePayWebButtonType.Order -> "order"
    ApplePayWebButtonType.Donate -> "donate"
    ApplePayWebButtonType.Continue -> "continue"
    ApplePayWebButtonType.CheckOut -> "check-out"
    ApplePayWebButtonType.Book -> "book"
    ApplePayWebButtonType.Subscribe -> "subscribe"
    ApplePayWebButtonType.AddMoney -> "add-money"
    ApplePayWebButtonType.TopUp -> "top-up"
    ApplePayWebButtonType.Reload -> "reload"
    ApplePayWebButtonType.Rent -> "rent"
    ApplePayWebButtonType.Support -> "support"
    ApplePayWebButtonType.Tip -> "tip"
    ApplePayWebButtonType.SetUp -> "set-up"
}
