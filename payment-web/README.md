# KPayment Web

Google Pay and Apple Pay for Kotlin/JS and Kotlin/Wasm.

## Install

```kotlin
dependencies {
    implementation("com.kttipay:kpayment-web:<version>")
}
```

## Quick Start

```kotlin
val config = WebPaymentConfig(
    environment = PaymentEnvironment.Development,
    googlePay = GooglePayConfig(
        merchantId = "YOUR_MERCHANT_ID",
        merchantName = "Your Store",
        gateway = "stripe",
        gatewayMerchantId = "YOUR_GATEWAY_ID"
    ),
    applePayWeb = ApplePayWebConfig(
        base = ApplePayBaseConfig(merchantName = "Your Store"),
        merchantValidationEndpoint = "https://example.com/apple-pay/validate",
        baseUrl = "https://example.com",
        domain = "example.com"
    )
)

@Composable
fun CheckoutWeb() {
    val manager = rememberWebPaymentManager(config)

    PaymentManagerProvider(manager) {
        val applePay = rememberApplePayWebLauncher { result -> /* handle result */ }
        val googlePay = rememberGooglePayWebLauncher { result -> /* handle result */ }

        Button(onClick = { googlePay.launch("10.00") }) { Text("Google Pay") }
        Button(onClick = { applePay.launch("10.00") }) { Text("Apple Pay") }
    }
}
```

## Non-Compose Usage

- `createWebPaymentManager(config)`

## Google Pay Script

Google Pay requires the [Google Pay JS SDK](https://developers.google.com/pay/api/web/guides/tutorial) (`pay.js`) to be loaded before `launch()` is called. Add this to your HTML `<head>`:

```html
<script src="https://pay.google.com/gp/p/js/pay.js"></script>
```

Without the script tag, `launch()` will fail with `Uncaught ReferenceError: google is not defined`. The SDK is otherwise lazy-loaded on the first capability check, so running an availability check before rendering the button also works.

## Apple Pay Cross-Browser (QR Code Flow)

By default (`enableJsSdk = true`), the library loads the [Apple Pay JS SDK](https://developer.apple.com/documentation/apple_pay_on_the_web) to enable Apple Pay on **all browsers** via QR code (iOS 18+). Without the SDK, Apple Pay is Safari-only.

For fastest loading, add the SDK script to your HTML `<head>`:

```html
<script src="https://applepay.cdn-apple.com/jsapi/1.latest/apple-pay-sdk.js"
        crossorigin="anonymous"></script>
```

Set `enableJsSdk = false` to disable dynamic SDK loading — Apple Pay will then only work if the script tag is present or in Safari.

### Apple Pay Button Options

| Option | Description |
|--------|-------------|
| `ApplePayWebButton` | Official Apple-branded `<apple-pay-button>` from the JS SDK. Renders via `HtmlElementView`. |
| `rememberApplePayWebLauncher` + any button | Full styling control using any Compose button. Same payment flow. |

Both use the same `ApplePaySession` API under the hood. The SDK polyfills `ApplePaySession` on non-Safari browsers, so `session.begin()` triggers the QR code flow from either button.

### ApplePayWebButton Usage

```kotlin
ApplePayWebButton(
    onClick = { launcher.launch("10.00") },
    modifier = Modifier.fillMaxWidth().height(48.dp),
    config = ApplePayWebButtonConfig(
        theme = ApplePayWebButtonTheme.Black,  // Black, White, WhiteOutline
        type = ApplePayWebButtonType.Pay,      // Pay, Buy, CheckOut, Donate, Subscribe, etc.
        cornerRadius = 8.dp,
        locale = "en-US"                       // null = browser locale
    )
)
```

Style the SDK button's internal height via CSS custom properties in your stylesheet:

```css
apple-pay-button {
    --apple-pay-button-width: 100%;
    --apple-pay-button-height: 48px;
    --apple-pay-button-border-radius: 8px;
}
```

### Custom Compose Button Usage

```kotlin
val launcher = rememberApplePayWebLauncher { result ->
    when (result) {
        is PaymentResult.Success -> println("Token: ${result.token}")
        is PaymentResult.Error -> println("Error: ${result.message}")
        is PaymentResult.Cancelled -> println("Cancelled")
    }
}

Button(onClick = { launcher.launch("10.00") }) {
    Text("Pay with Apple Pay")
}
```

## Track Payment State

```kotlin
val launcher = rememberApplePayWebLauncher { result -> /* handle */ }
val isProcessing by launcher.isProcessing.collectAsState()

Button(
    enabled = !isProcessing,
    onClick = { launcher.launch("10.00") }
) { Text("Apple Pay") }
```

## Notes

- Google Pay requires `pay.js` to be loaded before `launch()`. Either include the `<script>` tag in `<head>` or run a capability check first.
- Apple Pay requires HTTPS. Use ngrok or a staging domain for local testing.
- Apple Pay requires a merchant validation endpoint and domain verification with Apple.
- The Apple Pay JS SDK script must be loaded synchronously in `<head>` (not async/deferred).
- When `enableJsSdk = false` and no SDK `<script>` tag is present, Apple Pay is Safari-only.
- Concurrent launch attempts return `PaymentErrorReason.AlreadyInProgress`.

## License

Apache 2.0. See `LICENSE`.
