# KPayment Web Sample

This sample demonstrates how to integrate the KPayment library into a web application using Kotlin/JS and Kotlin/Wasm.

## Features

- ✅ Web Payment Manager initialization
- ✅ Google Pay capability checking
- ✅ Apple Pay capability checking
- ✅ Payment configuration UI
- ✅ Compose Multiplatform for Web
- ✅ Real-time capability status updates

## Project Structure

```
webMain/
├── kotlin/
│   └── com/kttipay/kpayment/
│       ├── Main.kt          # Application entry point
│       └── WebApp.kt        # Main UI composable
└── resources/
    └── index.html           # HTML wrapper
```

## Building and Running

### Prerequisites

1. Configure payment credentials in `commonMain/kotlin/com/kttipay/kpayment/config/PaymentConfig.kt`:
   - Google Pay merchant ID and gateway settings
   - Apple Pay domain and merchant validation endpoint

2. For Apple Pay:
   - Set up domain verification at https://developer.apple.com
   - Implement merchant validation endpoint on your backend
   - Configure the `baseUrl` and `merchantValidationEndpoint` in `WebApp.kt`

### Build for Kotlin/JS

```bash
./gradlew :sampleWeb:jsBrowserDevelopmentRun
```

This will:
- Compile the Kotlin code to JavaScript
- Start a development server
- Open your browser at http://localhost:8080

### Build for Kotlin/Wasm

```bash
./gradlew :sampleWeb:wasmJsBrowserDevelopmentRun
```

This will:
- Compile the Kotlin code to WebAssembly
- Start a development server
- Open your browser at http://localhost:8080

### Production Build

For Kotlin/JS:
```bash
./gradlew :sampleWeb:jsBrowserDistribution
```

For Kotlin/Wasm:
```bash
./gradlew :sampleWeb:wasmJsBrowserDistribution
```

The output will be in:
- JS: `sampleWeb/build/dist/js/productionExecutable/`
- Wasm: `sampleWeb/build/dist/wasmJs/productionExecutable/`

## Architecture

### WebPaymentManager

The web sample uses `WebPaymentManager` instead of `MobilePaymentManager`:

```kotlin
val paymentManager = createWebPaymentManager()

// Initialize with web-specific config
paymentManager.initialize(
    WebPaymentConfig(
        environment = PaymentEnvironment.Development,
        googlePay = GooglePayConfig(...),
        applePayWeb = ApplePayWebConfig(...)
    )
)

// Check capabilities
paymentManager.checkCapabilities()

// Observe capability changes
paymentManager.capabilities.collect { caps ->
    // React to capability changes
}
```

### Key Differences from Mobile

| Feature | Mobile | Web |
|---------|--------|-----|
| Manager | `MobilePaymentManager` | `WebPaymentManager` |
| Apple Pay Config | `ApplePayMobileConfig` | `ApplePayWebConfig` |
| Initialization | Synchronous | Async (requires `checkCapabilities()`) |
| Payment UI | Native | Browser-based |
| Merchant Validation | Automatic | Backend endpoint required |

## Apple Pay Web Setup

Apple Pay on the web requires additional setup:

### 1. Domain Verification

1. Go to https://developer.apple.com/account/resources/identifiers/list/merchant
2. Select your merchant ID
3. Add your domain(s)
4. Download the verification file
5. Host it at `https://yourdomain.com/.well-known/apple-developer-merchantid-domain-association`

### 2. Merchant Validation Endpoint

Create a backend endpoint that:
1. Receives the validation URL from Apple Pay JS
2. Makes a POST request to that URL with your merchant certificate
3. Returns the merchant session to your frontend

Example configuration in `WebApp.kt`:
```kotlin
ApplePayWebConfig(
    domain = "yourdomain.com",
    merchantValidationEndpoint = "/api/apple-pay/validate-merchant",
    baseUrl = "https://yourdomain.com",
    base = ApplePayBaseConfig(...)
)
```

## Google Pay Web Setup

Google Pay on the web requires:

1. **Register your domain** in the Google Pay Business Console
2. **Configure allowed payment methods** in `PaymentConfig.kt`
3. **Test environment**: Use `GooglePayEnvironment.TEST` during development
4. **Production**: Switch to `GooglePayEnvironment.PRODUCTION` for live payments

## Logging

The sample enables KPayment logging for debugging:

```kotlin
KPaymentLogger.enabled = true
KPaymentLogger.callback = object : KPaymentLogCallback {
    override fun onLog(event: LogEvent) {
        console.log("[${event.tag}] ${event.message}")
    }
}
```

Check the browser console for detailed payment flow logs.

## Testing

### Google Pay Testing
- Use test cards from: https://developers.google.com/pay/api/web/guides/test-and-deploy/test-card-suite
- Test environment supports test cards without actual charges

### Apple Pay Testing
- Requires Safari browser
- Test in a sandbox environment
- Use test cards from your Apple Developer account

## Troubleshooting

### "Google Pay not available"
- Ensure your domain is registered in Google Pay Business Console
- Check that you're using HTTPS (required for payment APIs)
- Verify payment gateway configuration

### "Apple Pay not available"
- Apple Pay only works in Safari and Safari-based browsers
- Requires HTTPS
- Domain must be verified
- Merchant validation endpoint must be accessible

### "CORS errors"
- Configure CORS headers on your backend
- Ensure merchant validation endpoint allows requests from your domain

## Resources

- [KPayment Documentation](../../README.md)
- [Google Pay Web Integration](https://developers.google.com/pay/api/web)
- [Apple Pay Web Integration](https://developer.apple.com/documentation/apple_pay_on_the_web)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
