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
- **Capability Detection** - Reactive availability checks via Flows
- **Type-Safe API** - Shared config and model types across platforms
- **Serializable Tokens** - kotlinx.serialization support for payment tokens

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
            // handle GooglePayWebResult.Success, .Error, .Cancelled
        }

        Button(onClick = { googlePay.launch("10.00") }) {
            Text("Pay with Google Pay")
        }
    }
}
```

## Module Details

### payment-core

Shared abstractions used by all platforms:

- `PaymentManager` and capability flow
- Config models (`GooglePayConfig`, `ApplePayBaseConfig`, `MobilePaymentConfig`, `WebPaymentConfig`)
- Result types (`PaymentResult`) and tokens (`GooglePayToken`, `ApplePayToken`)

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
val canUseGooglePay = manager.canUse(PaymentProvider.GooglePay)

manager.observeAvailability(PaymentProvider.GooglePay).collect { available ->
    // true when ready, false otherwise
}

manager.capabilitiesFlow.collect { caps ->
    when (caps.googlePay) {
        CapabilityStatus.Ready -> { /* show button */ }
        CapabilityStatus.NotConfigured -> { /* missing config */ }
        CapabilityStatus.NotSupported -> { /* platform not supported */ }
        CapabilityStatus.Checking -> { /* loading */ }
        is CapabilityStatus.Error -> { /* handle error */ }
    }
}
```

### Handle Payment Results

```kotlin
when (val result = paymentResult) {
    is PaymentResult.Success -> {
        val token = result.token
    }
    is PaymentResult.Error -> {
        when (result.reason) {
            PaymentErrorReason.Timeout -> {}
            PaymentErrorReason.NetworkError -> {}
            PaymentErrorReason.DeveloperError -> {}
            PaymentErrorReason.InternalError -> {}
            PaymentErrorReason.NotAvailable -> {}
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

### Refresh Capabilities

Re-check payment availability when user conditions change:

```kotlin
val newCapabilities = manager.refreshCapabilities()
```

### All PaymentManager Methods

```kotlin
manager.canUse(PaymentProvider.GooglePay)
manager.currentCapabilities()
manager.refreshCapabilities()
manager.observeAvailability(provider)
manager.capabilitiesFlow
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
