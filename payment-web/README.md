# KPayment Web

Google Pay and Apple Pay (Safari) for Kotlin/JS and Kotlin/Wasm.

## Install

```kotlin
dependencies {
    implementation("com.kttipay:kpayment-web:<version>")
}
```

## Quick start (Compose)

Amounts are decimal strings (for example, `"10.00"`).

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
        val googlePay = rememberGooglePayWebLauncher { result ->
            when (result) {
                is PaymentResult.Success -> println("Token: ${result.token}")
                is PaymentResult.Error -> println("Error: ${result.message}")
                is PaymentResult.Cancelled -> println("Cancelled")
            }
        }

        val applePay = rememberApplePayWebLauncher { result ->
            when (result) {
                is PaymentResult.Success -> println("Token: ${result.token}")
                is PaymentResult.Error -> println("Error: ${result.message}")
                is PaymentResult.Cancelled -> println("Cancelled")
            }
        }

        Button(onClick = { googlePay.launch("10.00") }) {
            Text("Pay with Google Pay")
        }

        Button(onClick = { applePay.launch("10.00") }) {
            Text("Pay with Apple Pay")
        }
    }
}
```

## Non-Compose usage

- `createWebPaymentManager(config)`

## Notes

- Apple Pay on web works in Safari only.
- Apple Pay requires a merchant validation endpoint for domain verification.

## License

Apache 2.0. See `LICENSE`.
