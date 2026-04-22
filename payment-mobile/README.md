# KPayment Mobile

Android and iOS payments with Google Pay (Android) and Apple Pay (iOS).

## Install

```kotlin
dependencies {
    implementation("com.kttipay:kpayment-mobile:<version>")
}
```

## Platform Setup

### Android

No additional setup. Google Play Services Wallet is included as a dependency.

### iOS

1. In Xcode, select your target → "Signing & Capabilities" → add "Apple Pay"
2. Select your [merchant ID](https://developer.apple.com/account/resources/identifiers/list/merchant) (format: `merchant.com.yourcompany.yourapp`)

## Quick Start (Compose)

Amounts are decimal strings (for example, `"10.00"`).

```kotlin
val config = MobilePaymentConfig(
    environment = PaymentEnvironment.Development,
    googlePay = GooglePayConfig(
        merchantId = "YOUR_MERCHANT_ID",
        merchantName = "Your Store",
        gateway = GatewayConfig.Stripe(publishableKey = "pk_live_...")
    ),
    applePayMobile = ApplePayMobileConfig(
        merchantId = "merchant.com.yourcompany.app",
        base = ApplePayBaseConfig(merchantName = "Your Store")
    )
)

@Composable
fun Checkout() {
    val manager = rememberMobilePaymentManager(config)

    PaymentManagerProvider(manager) {
        val launcher = rememberNativePaymentLauncher { result ->
            when (result) {
                is PaymentResult.Success -> println("Token: ${result.token}")
                is PaymentResult.Error -> println("Error: ${result.message}")
                is PaymentResult.Cancelled -> println("Cancelled")
            }
        }

        PaymentButton(
            theme = NativePaymentTheme.Dark,
            type = NativePaymentType.Pay,
            enabled = manager.canUse(currentNativePaymentProvider()),
            radius = 12.dp,
            onClick = { launcher.launch("10.00") }
        )
    }
}
```

## Non-Compose Usage

- Android: `createMobilePaymentManager(config, context)`
- iOS: `createMobilePaymentManager(config)`

## Track Payment State

```kotlin
val launcher = rememberNativePaymentLauncher { result -> /* handle */ }
val isProcessing by launcher.isProcessing.collectAsState()

PaymentButton(
    enabled = !isProcessing,
    onClick = { launcher.launch("10.00") }
)
```

## Compose Preview

`PaymentButton` works in `@Preview` out of the box. When `LocalInspectionMode` is active, a styled stub is rendered instead of the native button.

## Notes

- Apple Pay requires a physical iOS device and a valid merchant ID.
- Google Pay requires Google Play services and an eligible device.
- Zero-amount payments (`"0.00"`) are supported for card verification flows.
- Concurrent launch attempts return `PaymentErrorReason.AlreadyInProgress`.

## License

Apache 2.0. See `LICENSE`.
