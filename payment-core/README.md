# KPayment Core

Shared API and models used by the KPayment platform modules.

This module does not call any platform payment APIs. Use `kpayment-mobile` or
`kpayment-web` to create a payment manager.

## Install

```kotlin
dependencies {
    implementation("com.kttipay:kpayment-core:<version>")
}
```

## Includes

- `PaymentManager` interface with reactive and suspend APIs
- Config models: `GooglePayConfig`, `ApplePayBaseConfig`, `MobilePaymentConfig`, `WebPaymentConfig`
- Results and tokens: `PaymentResult`, `GooglePayToken`, `ApplePayToken`
- Capability detection: `CapabilityStatus`, `PaymentCapabilities`
- Error reasons: `PaymentErrorReason`

## PaymentManager API

| Method | Type | Description |
|--------|------|-------------|
| `config` | Property | The payment configuration |
| `checkCapabilities()` | `suspend` | Check current payment capabilities from platform SDKs |
| `observeCapabilities()` | `Flow<PaymentCapabilities>` | Reactively observe full payment capabilities |
| `observeAvailability(provider)` | `Flow<Boolean>` | Reactively observe specific provider availability |

## Configuration

```kotlin
// Google Pay
val googlePay = GooglePayConfig(
    merchantId = "YOUR_MERCHANT_ID",
    merchantName = "Your Store",
    gateway = GatewayConfig.Stripe(publishableKey = "pk_live_..."),
    allowedCardNetworks = setOf(GooglePayCardNetwork.VISA, GooglePayCardNetwork.MASTERCARD),
    allowedAuthMethods = GooglePayAuthMethod.DEFAULT,
    currencyCode = "AUD",
    countryCode = "AU",
    allowCreditCards = false,              // allow credit cards (default: false)
    assuranceDetailsRequired = false       // request cardholder verification (default: false)
)

// Apple Pay (shared base)
val appleBase = ApplePayBaseConfig(
    merchantName = "Your Store",
    supportedNetworks = setOf(ApplePayNetwork.VISA, ApplePayNetwork.MASTERCARD),
    merchantCapabilities = setOf(ApplePayMerchantCapability.CAPABILITY_3DS),
    currencyCode = "AUD",
    countryCode = "AU"
)
```

## Error Handling

```kotlin
when (result) {
    is PaymentResult.Success -> sendTokenToBackend(result.token)
    is PaymentResult.Error -> handleError(result.reason, result.message)
    is PaymentResult.Cancelled -> { /* no action needed */ }
}
```

| Reason | Suggested Action |
|--------|------------------|
| `NetworkError` | Check network, retry |
| `NotAvailable` | Show alternative payment |
| `AlreadyInProgress` | Disable button via `launcher.isProcessing` |
| `DeveloperError` | Fix configuration |
| `Timeout` | Retry after delay |
| `SignInRequired` | Prompt sign-in |
| `InternalError` | Retry with backoff |
| `ApiNotConnected` | Re-initialize manager |
| `ConnectionSuspendedDuringCall` | Retry on resume |
| `Interrupted` | Show retry dialog |

## License

Apache 2.0. See `LICENSE`.
