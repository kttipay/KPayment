# KPayment Mobile

Android and iOS payments with Google Pay (Android) and Apple Pay (iOS).

## Install

```kotlin
dependencies {
    implementation("com.kttipay:kpayment-mobile:<version>")
}
```

## Quick start (Compose)

Amounts are decimal strings (for example, `"10.00"`).

```kotlin
val config = MobilePaymentConfig(
    environment = PaymentEnvironment.Development,
    googlePay = GooglePayConfig(
        merchantId = "YOUR_MERCHANT_ID",
        merchantName = "Your Store",
        gateway = "stripe",
        gatewayMerchantId = "YOUR_GATEWAY_ID"
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
            // handle PaymentResult.Success, PaymentResult.Error, PaymentResult.Cancelled
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

## Non-Compose usage

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

## Notes

- Apple Pay requires a physical iOS device and a valid merchant ID.
- Google Pay requires Google Play services and an eligible device.
- Concurrent launch attempts return `PaymentErrorReason.AlreadyInProgress`.

## License

Apache 2.0. See `LICENSE`.
