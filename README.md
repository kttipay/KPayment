# KPayment

A Kotlin Multiplatform library for seamless payment processing across Android, iOS, and Web platforms. KPayment provides a unified API for integrating Google Pay and Apple Pay into your applications.

## Table of Contents

- [Features](#features)
- [Platform Support](#platform-support)
- [Architecture](#architecture)
- [Requirements](#requirements)
- [Installation](#installation)
- [Quick Start](#quick-start)
  - [Android](#android)
  - [iOS](#ios)
  - [Web](#web)
- [Module Details](#module-details)
- [Configuration](#configuration)
  - [Google Pay Configuration](#google-pay-configuration)
  - [Apple Pay Configuration](#apple-pay-configuration)
- [Usage Examples](#usage-examples)
- [Samples](#samples)
- [License](#license)

## Features

- **Cross-Platform Payment Processing** - Single codebase for Android, iOS, and Web
- **Google Pay Integration** - Android and Web support
- **Apple Pay Integration** - iOS and Safari (Web) support
- **Compose Support** - Payment button + launcher helpers
- **Capability Detection** - Reactive availability checks via `Flow`
- **Suspend API** - `checkCapabilities()` delegates to platform SDKs
- **Type-Safe API** - Shared config and model types across platforms
- **Serializable Tokens** - kotlinx.serialization support for payment tokens
- **Thread-Safe** - Proper synchronization and concurrency handling across all platforms
- **Production-Ready** - Comprehensive error details and robust state management

## Platform Support

| Platform | Google Pay | Apple Pay | Compose UI |
|----------|------------|-----------|------------|
| Android  | ✅         | ❌        | ✅         |
| iOS      | ❌         | ✅        | ✅         |
| Web (JS) | ✅         | ✅*       | ✅         |
| WASM     | ✅         | ✅*       | ✅         |

*Apple Pay on Web requires Safari and a merchant validation endpoint.

## Architecture

KPayment is organized into three main modules:

```
KPayment/
├── payment-core/      Shared interfaces, models, and types
├── payment-mobile/    Android + iOS implementations
└── payment-web/       Web (JS/WASM) implementations
```

## Requirements

### Android
- Minimum SDK: 26
- Target SDK: 36
- Compile SDK: 36
- Google Play Services Wallet: 19.5.0+

### iOS
- Deployment target is set by your host app
- Apple Pay requires a physical device and a valid merchant ID

### Web
- Modern browsers with ES6 support
- Apple Pay requires Safari and domain validation

### Build Environment
- Kotlin: 2.3.x
- Java: 21
- Gradle: 8.13+

## Installation

### Add Maven Central Repository

Ensure Maven Central is in your repository list (usually already configured):

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
```

### Add Dependencies

Add the KPayment dependencies to your `build.gradle.kts`:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.kttipay:kpayment-core:0.1.0")
        }

        androidMain.dependencies {
            implementation("com.kttipay:kpayment-mobile:0.1.0")
        }

        iosMain.dependencies {
            implementation("com.kttipay:kpayment-mobile:0.1.0")
        }

        jsMain.dependencies {
            implementation("com.kttipay:kpayment-web:0.1.0")
        }

        wasmJsMain.dependencies {
            implementation("com.kttipay:kpayment-web:0.1.0")
        }
    }
}
```

## Platform-Specific Setup

### Android

No additional configuration required. Google Play Services Wallet is included as a dependency.

### iOS

1. **Add Apple Pay Capability:**
   - In Xcode, select your target
   - Go to "Signing & Capabilities"
   - Click "+ Capability" and add "Apple Pay"
   - Select your merchant ID

2. **Configure Merchant ID:**
   - Create a merchant ID in your [Apple Developer account](https://developer.apple.com/account/resources/identifiers/list/merchant)
   - Format: `merchant.com.yourcompany.yourapp`

### Web

**For Apple Pay on Web:**
- Register your domain with Apple
- Implement merchant validation endpoint on your backend
- Host domain verification file

See [Apple Pay on the Web documentation](https://developer.apple.com/documentation/apple_pay_on_the_web)

## Quick Start

Amounts are decimal strings (for example, `"10.00"`).

### Android

```kotlin
val config = MobilePaymentConfig(
    environment = PaymentEnvironment.Development,
    googlePay = GooglePayConfig(
        merchantId = "YOUR_MERCHANT_ID",
        merchantName = "Your Store",
        gateway = "stripe",
        gatewayMerchantId = "YOUR_GATEWAY_ID"
    ),
    applePayMobile = null
)

@Composable
fun PaymentScreen() {
    val manager = rememberMobilePaymentManager(config)
    val isReady by manager.observeAvailability(currentNativePaymentProvider())
        .collectAsState(initial = false)

    PaymentManagerProvider(manager) {
        val launcher = rememberNativePaymentLauncher { result ->
            // handle PaymentResult.Success, PaymentResult.Error, PaymentResult.Cancelled
        }

        PaymentButton(
            theme = NativePaymentTheme.Dark,
            type = NativePaymentType.Pay,
            enabled = isReady,
            radius = 12.dp,
            onClick = { launcher.launch("10.00") }
        )
    }
}
```

### iOS

```kotlin
val config = MobilePaymentConfig(
    environment = PaymentEnvironment.Development,
    googlePay = null,
    applePayMobile = ApplePayMobileConfig(
        merchantId = "merchant.com.yourcompany.app",
        base = ApplePayBaseConfig(merchantName = "Your Store")
    )
)

@Composable
fun PaymentScreen() {
    val manager = rememberMobilePaymentManager(config)
    val isReady by manager.observeAvailability(currentNativePaymentProvider())
        .collectAsState(initial = false)

    PaymentManagerProvider(manager) {
        val launcher = rememberNativePaymentLauncher { result ->
            // handle PaymentResult.Success, PaymentResult.Error, PaymentResult.Cancelled
        }

        PaymentButton(
            theme = NativePaymentTheme.Dark,
            type = NativePaymentType.Pay,
            enabled = isReady,
            radius = 12.dp,
            onClick = { launcher.launch("10.00") }
        )
    }
}
```

### Web

Use `createWebPaymentManager(config)` outside Compose if you are not using Compose UI.

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
fun PaymentScreen() {
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

## Module Details

### payment-core

Shared abstractions used by all platforms:

- `PaymentManager` - unified API for capability checks and configuration
- Config models (`GooglePayConfig`, `ApplePayBaseConfig`, `MobilePaymentConfig`, `WebPaymentConfig`)
- Result types (`PaymentResult`) and tokens (`GooglePayToken`, `ApplePayToken`)
- Reactive capability observation via `StateFlow` and `Flow`

See `payment-core/README.md` for a focused overview.

### payment-mobile

Android + iOS implementation with Compose helpers:

- `createMobilePaymentManager(...)`
- `rememberMobilePaymentManager(...)`
- `PaymentButton` and `rememberNativePaymentLauncher(...)`

See `payment-mobile/README.md` for setup and platform-specific details.

### payment-web

Web implementation for JS/Wasm:

- `createWebPaymentManager(...)`
- `rememberWebPaymentManager(...)`
- `rememberGooglePayWebLauncher(...)` and `rememberApplePayWebLauncher(...)`

See `payment-web/README.md` for setup and platform-specific details.

## Logging

KPayment includes an optional logging system for debugging:

```kotlin
KPaymentLogger.enabled = true

KPaymentLogger.callback = object : KPaymentLogCallback {
    override fun onLog(event: LogEvent) {
        println("[${event.tag}] ${event.message}")
    }
}
```

By default, logging is **disabled** and will not interfere with your app's logging.

## Configuration

### Google Pay Configuration

```kotlin
val googlePay = GooglePayConfig(
    merchantId = "YOUR_MERCHANT_ID",
    merchantName = "Your Store",
    gateway = "stripe",
    gatewayMerchantId = "YOUR_GATEWAY_ID",
    allowedCardNetworks = setOf(
        GooglePayCardNetwork.VISA,
        GooglePayCardNetwork.MASTERCARD
    ),
    allowedAuthMethods = GooglePayAuthMethod.DEFAULT,
    currencyCode = "AUD",
    countryCode = "AU"
)
```

#### Advanced Google Pay Options

```kotlin
val googlePay = GooglePayConfig(
    allowCreditCards = true,
    assuranceDetailsRequired = true
)
```

- `allowCreditCards`: Set to `true` to allow credit card transactions (default: `false`)
- `assuranceDetailsRequired`: Set to `true` to request additional cardholder verification (default: `false`)

### Apple Pay Configuration

```kotlin
val appleBase = ApplePayBaseConfig(
    merchantName = "Your Store",
    supportedNetworks = setOf(
        ApplePayNetwork.VISA,
        ApplePayNetwork.MASTERCARD
    ),
    merchantCapabilities = setOf(
        ApplePayMerchantCapability.CAPABILITY_3DS,
        ApplePayMerchantCapability.CAPABILITY_DEBIT
    ),
    currencyCode = "AUD",
    countryCode = "AU"
)

val applePayMobile = ApplePayMobileConfig(
    merchantId = "merchant.com.yourcompany.app",
    base = appleBase
)

val applePayWeb = ApplePayWebConfig(
    base = appleBase,
    merchantValidationEndpoint = "https://example.com/apple-pay/validate",
    baseUrl = "https://example.com",
    domain = "example.com"
)
```

## Usage Examples

### Check Payment Capability

```kotlin
// Reactive: Observe availability changes (most common)
val isReady by manager.observeAvailability(PaymentProvider.GooglePay)
    .collectAsState(initial = false)

Button(
    enabled = isReady,
    onClick = { launcher.launch("10.00") }
) {
    Text("Pay with Google Pay")
}

// Explicit check when needed
val capabilities = manager.checkCapabilities()
if (capabilities.canPayWith(PaymentProvider.GooglePay)) {
    launcher.launch("10.00")
}

// Handle detailed capability status
val capabilities = manager.checkCapabilities()
when (capabilities.googlePay) {
    CapabilityStatus.Ready -> { /* show button */ }
    CapabilityStatus.NotConfigured -> { /* missing config */ }
    CapabilityStatus.NotSupported -> { /* platform not supported */ }
    CapabilityStatus.Checking -> { /* loading */ }
    is CapabilityStatus.Error -> { /* handle error: ${error.reason} */ }
}
```

### Handle Payment Results

```kotlin
when (val result = paymentResult) {
    is PaymentResult.Success -> {
        val token = result.token
        // Send token to your backend for processing
    }
    is PaymentResult.Error -> {
        when (result.reason) {
            PaymentErrorReason.Timeout -> {}
            PaymentErrorReason.NetworkError -> {}
            PaymentErrorReason.DeveloperError -> {}
            PaymentErrorReason.InternalError -> {}
            PaymentErrorReason.NotAvailable -> {}
            PaymentErrorReason.AlreadyInProgress -> {}
            PaymentErrorReason.SignInRequired -> {}
            PaymentErrorReason.ApiNotConnected -> {}
            PaymentErrorReason.ConnectionSuspendedDuringCall -> {}
            PaymentErrorReason.Interrupted -> {}
            PaymentErrorReason.Unknown -> {}
        }
    }
    is PaymentResult.Cancelled -> {}
}
```

## Error Handling

KPayment provides comprehensive error handling through the [PaymentResult.Error] sealed class. Understanding error types and implementing proper error handling is crucial for a robust payment integration.

### Error Types

#### Timeout
Occurs when a payment request times out. This is typically a temporary issue.

**Recommended Action**: Retry the payment request after a short delay.

```kotlin
is PaymentErrorReason.Timeout -> {
    // Retry after delay
    delay(2000)
    retryPayment()
}
```

#### NetworkError
Occurs when there's a network connectivity issue during payment processing.

**Recommended Action**: Check network connectivity and retry.

```kotlin
is PaymentErrorReason.NetworkError -> {
    if (isNetworkAvailable()) {
        retryPayment()
    } else {
        showNetworkError()
    }
}
```

#### DeveloperError
Indicates a configuration or implementation error. This usually means something is misconfigured.

**Recommended Action**: Review your payment configuration and ensure all required parameters are set correctly.

```kotlin
is PaymentErrorReason.DeveloperError -> {
    Log.e("Payment", "Configuration error: ${result.message}")
    // Review payment configuration
    checkPaymentConfiguration()
}
```

#### InternalError
An internal error occurred in the payment system. This is typically temporary.

**Recommended Action**: Retry after a delay, or contact support if persistent.

```kotlin
is PaymentErrorReason.InternalError -> {
    // Retry with exponential backoff
    retryWithBackoff()
}
```

#### NotAvailable
The payment method is not available on this device or platform.

**Recommended Action**: Check payment capabilities before attempting payment, or show an alternative payment method.

```kotlin
is PaymentErrorReason.NotAvailable -> {
    val capabilities = manager.checkCapabilities()
    if (!capabilities.canPayWith(PaymentProvider.GooglePay)) {
        showAlternativePaymentMethod()
    }
}
```

#### AlreadyInProgress
A payment is already being processed.

**Recommended Action**: Observe `launcher.isProcessing` to disable the button during payment, or ignore this error.

```kotlin
is PaymentErrorReason.AlreadyInProgress -> {
    // Payment already in progress, ignore or show message
}
```

#### SignInRequired
User sign-in is required to complete the payment.

**Recommended Action**: Prompt the user to sign in and retry.

```kotlin
is PaymentErrorReason.SignInRequired -> {
    promptUserSignIn {
        retryPayment()
    }
}
```

#### ApiNotConnected
The payment API is not connected or initialized.

**Recommended Action**: Ensure the payment manager is properly initialized before use.

```kotlin
is PaymentErrorReason.ApiNotConnected -> {
    // Reinitialize payment manager
    initializePaymentManager()
}
```

#### ConnectionSuspendedDuringCall
The connection was suspended during the payment call (e.g., app backgrounded).

**Recommended Action**: Retry the payment when the app returns to foreground.

```kotlin
is PaymentErrorReason.ConnectionSuspendedDuringCall -> {
    // Retry when app resumes
    lifecycleScope.launchWhenResumed {
        retryPayment()
    }
}
```

#### Interrupted
The payment operation was interrupted.

**Recommended Action**: Retry if appropriate, or inform the user.

```kotlin
is PaymentErrorReason.Interrupted -> {
    // Check if user wants to retry
    showRetryDialog()
}
```

#### Unknown
An unknown error occurred. Check the error message for details.

**Recommended Action**: Log the error and check the error message for additional context.

```kotlin
is PaymentErrorReason.Unknown -> {
    Log.e("Payment", "Unknown error: ${result.message}")
    // Check error message for details
    handleUnknownError(result.message)
}
```

### Track Payment State

The launcher exposes `isProcessing: StateFlow<Boolean>` to track whether a payment is in progress:

```kotlin
val launcher = rememberNativePaymentLauncher { result -> /* handle */ }
val isProcessing by launcher.isProcessing.collectAsState()

PaymentButton(
    enabled = isReady && !isProcessing,
    onClick = { launcher.launch("10.00") }
)
```

### Error Handling Best Practices

1. **Always Check Capabilities First**: Before attempting payment, check if the payment method is available:
   ```kotlin
   val capabilities = manager.checkCapabilities()
   if (capabilities.canPayWith(PaymentProvider.GooglePay)) {
       launchPayment()
   } else {
       showPaymentNotAvailable()
   }
   ```

2. **Implement Retry Logic**: For transient errors (Timeout, NetworkError, InternalError), implement retry logic with exponential backoff:
   ```kotlin
   suspend fun retryPayment(maxRetries: Int = 3) {
       repeat(maxRetries) { attempt ->
           try {
               val result = launchPayment()
               if (result is PaymentResult.Success) return
           } catch (e: Exception) {
               if (attempt == maxRetries - 1) throw e
               delay(1000L * (attempt + 1))
           }
       }
   }
   ```

3. **Handle User Cancellation Gracefully**: User cancellation is not an error - handle it appropriately:
   ```kotlin
   is PaymentResult.Cancelled -> {
       // User cancelled - this is normal, no action needed
       // Optionally show a message or return to previous screen
   }
   ```

4. **Log Errors for Debugging**: Always log errors with context for debugging:
   ```kotlin
   is PaymentResult.Error -> {
       Log.e("Payment", "Payment failed: ${result.reason}, message: ${result.message}")
       // Handle error
   }
   ```

5. **Provide User Feedback**: Inform users about errors in a user-friendly way:
   ```kotlin
   is PaymentResult.Error -> {
       when (result.reason) {
           PaymentErrorReason.NetworkError -> {
               showError("Network error. Please check your connection and try again.")
           }
           PaymentErrorReason.NotAvailable -> {
               showError("Payment method not available. Please use an alternative.")
           }
           else -> {
               showError("Payment failed. Please try again.")
           }
       }
   }
   ```

### PaymentManager API

| Method | Type | Description |
|--------|------|-------------|
| `config` | Property | The payment configuration |
| `checkCapabilities()` | `suspend` | Check current payment capabilities from platform SDKs |
| `observeCapabilities()` | `Flow<PaymentCapabilities>` | Reactively observe full payment capabilities |
| `observeAvailability(provider)` | `Flow<Boolean>` | Reactively observe specific provider availability |

```kotlin
// Reactive UI for full capabilities
val capabilities by manager.observeCapabilities()
    .collectAsState(initial = PaymentCapabilities.initial)

when (capabilities.googlePay) {
    CapabilityStatus.Ready -> ShowPaymentButton()
    CapabilityStatus.Checking -> ShowLoading()
    else -> ShowNotAvailable()
}

// Reactive UI for single provider (convenience)
val isReady by manager.observeAvailability(PaymentProvider.GooglePay)
    .collectAsState(initial = false)

Button(enabled = isReady, onClick = { launcher.launch("10.00") }) {
    Text("Pay")
}

// Explicit capability check
val capabilities = manager.checkCapabilities()
if (capabilities.canPayWith(PaymentProvider.GooglePay)) {
    launcher.launch("10.00")
}
```

## Samples

- `sampleMobile` - Android + iOS KMP sample
- `sampleWeb` - Web sample (JS/Wasm)
- `iosApp` - iOS host app for the KMP sample

Sample configs:
- `sampleMobile/src/commonMain/kotlin/com/kttipay/kpayment/config/PaymentConfig.kt`
- `sampleWeb/src/webMain/kotlin/com/kttipay/kpayment/PaymentConfig.kt`

## License

Apache 2.0. See `LICENSE`.
