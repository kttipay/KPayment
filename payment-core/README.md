# KPayment Core

Shared API and models used by the KPayment platform modules.

This module does not call any platform payment APIs. Use `kpayment-mobile` or
`kpayment-web` to create a payment manager.

## Includes

- `PaymentManager` interface and capability flow
- Config models: `GooglePayConfig`, `ApplePayBaseConfig`, `MobilePaymentConfig`, `WebPaymentConfig`
- Results and tokens: `PaymentResult`, `GooglePayToken`, `ApplePayToken`

## Install

```kotlin
dependencies {
    implementation("com.kttipay:kpayment-core:<version>")
}
```

## Example config

```kotlin
val googlePay = GooglePayConfig(
    merchantId = "YOUR_MERCHANT_ID",
    merchantName = "Your Store",
    gateway = "stripe",
    gatewayMerchantId = "YOUR_GATEWAY_ID"
)

val appleBase = ApplePayBaseConfig(merchantName = "Your Store")

val mobileConfig = MobilePaymentConfig(
    environment = PaymentEnvironment.Development,
    googlePay = googlePay,
    applePayMobile = ApplePayMobileConfig(
        merchantId = "merchant.com.yourcompany.app",
        base = appleBase
    )
)
```

## License

Apache 2.0. See `LICENSE`.
