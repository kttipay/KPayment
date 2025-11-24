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
- [Sample App](#sample-app)
- [License](#license)

## Features

- **Cross-Platform Payment Processing** - Single codebase for Android, iOS, and Web
- **Google Pay Integration** - Full support on Android and Web platforms
- **Apple Pay Integration** - Native support on iOS and Safari (Web)
- **Jetpack Compose Support** - Ready-to-use composable payment buttons and launchers
- **Reactive State Management** - Built with Kotlin Flows for real-time capability checking
- **Type-Safe API** - Leverages Kotlin's type system for compile-time safety
- **Capability Detection** - Automatic checking of payment provider availability
- **Dependency Injection Ready** - Koin support out of the box
- **Serializable Tokens** - kotlinx.serialization support for payment tokens

## Platform Support

| Platform | Google Pay | Apple Pay | Compose UI |
|----------|------------|-----------|------------|
| Android  | ✅         | ❌        | ✅         |
| iOS      | ❌         | ✅        | ✅         |
| Web (JS) | ✅         | ✅*       | ✅         |
| WASM     | ✅         | ✅*       | ✅         |

*Apple Pay on Web requires Safari browser and valid merchant identity certificate

## Architecture

KPayment is organized into three main modules:

```
KPayment/
├── payment-core/      # Shared interfaces, models, and types
├── payment-mobile/    # Android & iOS implementations
└── payment-web/       # Web (JS/WASM) implementations
```

### payment-core
Contains platform-agnostic interfaces and models:
- `PaymentManager` - Base interface for all payment managers
- `PaymentLauncher` - Interface for launching payment flows
- Payment models (`GooglePayToken`, `ApplePayToken`)
- Configuration classes for all payment providers
- Result types (`PaymentResult.Success`, `PaymentResult.Error`, `PaymentResult.Cancelled`)

### payment-mobile
Mobile-specific implementations:
- `MobilePaymentManager` - Unified mobile payment manager
- Native Google Pay integration for Android
- Native Apple Pay integration for iOS
- Composable UI components (`GooglePayButton`, `ApplePayButton`)
- Platform-specific capability checking

### payment-web
Web-specific implementations:
- `WebPaymentManager` - Browser-based payment manager
- Google Pay for all modern browsers
- Apple Pay for Safari
- Compose for Web support
- Script loader utilities for async API initialization

## Requirements

### Android
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 36
- Compile SDK: 36
- Google Play Services Wallet: 19.5.0+

### iOS
- iOS 13.0+
- Xcode 14.0+
- CocoaPods or SPM

### Web
- Modern browsers with ES6 support
- Safari for Apple Pay

### Build Environment
- Kotlin: 2.2.21
- Java: 21
- Gradle: 8.13.1+

## Installation

Add the KPayment dependencies to your `build.gradle.kts`:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.kttipay:payment-core:$version")
        }

        androidMain.dependencies {
            implementation("com.kttipay:payment-mobile:$version")
        }

        iosMain.dependencies {
            implementation("com.kttipay:payment-mobile:$version")
        }

        jsMain.dependencies {
            implementation("com.kttipay:payment-web:$version")
        }
    }
}
```

## Quick Start

### Android

```kotlin
import com.kttipay.payment.mobile.MobilePaymentManager
import com.kttipay.payment.api.GooglePayConfig
import androidx.compose.runtime.Composable

@Composable
fun PaymentScreen() {
    val paymentManager = remember { MobilePaymentManager() }
    val launcher = rememberNativePaymentLauncher(
        onSuccess = { token ->
            // Handle successful payment
            println("Payment token: ${token.token}")
        },
        onError = { error ->
            // Handle error
            println("Payment error: ${error.message}")
        },
        onCancelled = {
            // Handle cancellation
            println("Payment cancelled")
        }
    )

    GooglePayButton(
        onClick = {
            val config = GooglePayConfig(
                merchantName = "Your Store",
                gatewayMerchantId = "your_merchant_id",
                amount = 10.00,
                currencyCode = "USD"
            )
            launcher.launch(config)
        }
    )
}
```

### iOS

```kotlin
import com.kttipay.payment.mobile.MobilePaymentManager
import com.kttipay.payment.api.ApplePayConfig
import androidx.compose.runtime.Composable

@Composable
fun PaymentScreen() {
    val paymentManager = remember { MobilePaymentManager() }
    val launcher = rememberNativePaymentLauncher(
        onSuccess = { token ->
            // Handle successful payment
            println("Payment token: ${token.token}")
        },
        onError = { error ->
            // Handle error
            println("Payment error: ${error.message}")
        },
        onCancelled = {
            // Handle cancellation
        }
    )

    ApplePayButton(
        onClick = {
            val config = ApplePayConfig(
                merchantIdentifier = "merchant.com.yourcompany",
                amount = 10.00,
                currencyCode = "USD",
                countryCode = "US"
            )
            launcher.launch(config)
        }
    )
}
```

### Web

```kotlin
import com.kttipay.payment.web.WebPaymentManager
import com.kttipay.payment.api.GooglePayConfig
import androidx.compose.runtime.Composable

@Composable
fun PaymentScreen() {
    val launcher = rememberGooglePayWebLauncher(
        onSuccess = { token ->
            // Handle successful payment
            console.log("Payment token: ${token.token}")
        },
        onError = { error ->
            // Handle error
            console.error("Payment error: ${error.message}")
        },
        onCancelled = {
            // Handle cancellation
        }
    )

    Button(
        onClick = {
            val config = GooglePayConfig(
                merchantName = "Your Store",
                gatewayMerchantId = "your_merchant_id",
                amount = 10.00,
                currencyCode = "USD"
            )
            launcher.launch(config)
        }
    ) {
        Text("Pay with Google Pay")
    }
}
```

## Module Details

### payment-core

The core module provides shared abstractions:

**Key Interfaces:**
- `PaymentManager` - Main interface for payment operations
- `PaymentLauncher` - Interface for launching payment flows
- `PaymentCapabilities` - Check payment provider availability

**Key Models:**
- `GooglePayToken` - Encrypted Google Pay payment token
- `ApplePayToken` - Apple Pay payment token
- `PaymentResult` - Sealed result type (Success, Error, Cancelled)

**Key Enums:**
- `PaymentProvider` - GooglePay, ApplePay
- `CapabilityStatus` - Available, Unavailable, Unknown

### payment-mobile

Mobile-specific implementations with Compose UI support:

**Features:**
- `MobilePaymentManager` - Unified manager for mobile platforms
- `rememberNativePaymentLauncher()` - Compose launcher with lifecycle awareness
- `GooglePayButton` - Pre-styled Google Pay button (Android)
- `ApplePayButton` - Pre-styled Apple Pay button (iOS)
- `observeAvailability()` - Flow-based capability monitoring

### payment-web

Web-specific implementations for browser environments:

**Features:**
- `WebPaymentManager` - Browser-based payment manager
- `WebPaymentManagerProvider` - Global singleton provider
- `rememberGooglePayWebLauncher()` - Compose launcher for Google Pay
- `rememberApplePayWebLauncher()` - Compose launcher for Apple Pay
- Async script loading for payment APIs

## Configuration

### Google Pay Configuration

```kotlin
val config = GooglePayConfig(
    merchantName = "Your Store Name",
    gatewayMerchantId = "your_gateway_merchant_id",
    gateway = "example", // Your payment gateway
    amount = 99.99,
    currencyCode = "USD",
    countryCode = "US",
    allowedCardNetworks = listOf("VISA", "MASTERCARD", "AMEX"),
    allowedAuthMethods = listOf("PAN_ONLY", "CRYPTOGRAM_3DS"),
    environment = "TEST" // or "PRODUCTION"
)
```

### Apple Pay Configuration

```kotlin
val config = ApplePayConfig(
    merchantIdentifier = "merchant.com.yourcompany.app",
    amount = 99.99,
    currencyCode = "USD",
    countryCode = "US",
    supportedNetworks = listOf("visa", "masterCard", "amex"),
    merchantCapabilities = listOf("3DS", "debit", "credit")
)
```

## Usage Examples

### Check Payment Capability

```kotlin
val paymentManager = MobilePaymentManager()

// Check if Google Pay is available
val isGooglePayAvailable = paymentManager.isPaymentProviderAvailable(
    PaymentProvider.GooglePay
)

// Observe availability changes
paymentManager.observeAvailability(PaymentProvider.GooglePay)
    .collect { status ->
        when (status) {
            CapabilityStatus.Available -> // Show payment button
            CapabilityStatus.Unavailable -> // Hide payment button
            CapabilityStatus.Unknown -> // Loading state
        }
    }
```

### Handle Payment Results

```kotlin
when (val result = paymentResult) {
    is PaymentResult.Success -> {
        val token = result.token
        // Send token to your backend for processing
        processPayment(token)
    }
    is PaymentResult.Error -> {
        // Show error message
        showError(result.message)
    }
    is PaymentResult.Cancelled -> {
        // User cancelled payment
        showMessage("Payment cancelled")
    }
}
```

## Sample App

A comprehensive sample application demonstrating KPayment usage is included in the `/sample` directory. The sample app showcases platform-specific payment integration with Google Pay (Android) and Apple Pay (iOS).

### Sample App Features

The sample app includes:
- **Payment Demo Screen** - Platform-specific payment buttons and launchers
- **Configuration Screen** - View all payment configuration values
- **Interactive UI** - Test different payment amounts
- **Result Handling** - See success, error, and cancellation states
- **Detailed Instructions** - In-app guidance for setup

### Configuration Setup

Before running the sample app, configure your payment credentials:

#### 1. Update PaymentConfig.kt

Edit `/sample/src/commonMain/kotlin/com/kttipay/kpayment/config/PaymentConfig.kt`:

```kotlin
// Google Pay Configuration
const val GOOGLE_PAY_MERCHANT_NAME = "Your Store Name"
const val GOOGLE_PAY_GATEWAY_MERCHANT_ID = "your_gateway_merchant_id"
const val GOOGLE_PAY_GATEWAY = "stripe" // or your gateway
const val GOOGLE_PAY_ENVIRONMENT = "TEST" // or "PRODUCTION"

// Apple Pay Configuration
const val APPLE_PAY_MERCHANT_ID = "merchant.com.yourcompany.app"

// Common Settings
const val PAYMENT_AMOUNT = 10.00
const val CURRENCY_CODE = "USD"
const val COUNTRY_CODE = "US"
```

#### 2. Obtain Google Pay Credentials

1. Visit [Google Pay Business Console](https://pay.google.com/business/console)
2. Create or select your merchant account
3. Configure your payment gateway (Stripe, Braintree, etc.)
4. Get your gateway merchant ID from your payment processor
5. Use `TEST` environment for development

#### 3. Obtain Apple Pay Credentials

1. Visit [Apple Developer Portal](https://developer.apple.com/account)
2. Go to **Certificates, Identifiers & Profiles**
3. Create a new **Merchant ID** (format: `merchant.com.yourcompany.app`)
4. Create a **Payment Processing Certificate**
5. In Xcode:
   - Open `/iosApp/iosApp.xcodeproj`
   - Select your target
   - Go to **Signing & Capabilities**
   - Add **Apple Pay** capability
   - Select your merchant ID

#### 4. Apple Pay Automatic Integration (iOS)

**No Setup Required!** The payment-mobile library automatically handles Apple Pay integration using PassKit.

**How it works:**
- The library includes a built-in Kotlin/Native PassKit implementation
- Apple Pay is automatically initialized when you create `MobilePaymentManager`
- Direct integration with iOS PassKit APIs - no Swift/Objective-C bridging needed
- No manual factory registration required in your app

**What's included in the library:**
- Pure Kotlin/Native PassKit integration (`PKPaymentAuthorizationController`)
- Payment token extraction and JSON conversion
- Complete error handling and result callbacks
- Automatic initialization when Apple Pay config is present

**Optional Customization:**
If you need custom behavior, you can provide your own factory implementation:
```kotlin
// Optional - only if you need custom behavior
// Set this BEFORE creating MobilePaymentManager or launching payments
IosApplePayManager.setCustomFactory(YourCustomFactory())
```

**Implementation Location:**
- `/payment-mobile/src/iosMain/kotlin/.../KotlinNativeApplePayFactory.kt` - Built-in PassKit integration
- `/payment-mobile/src/iosMain/kotlin/.../ApplePayViewControllerProvider.kt` - Automatic VC discovery

### Run Android Sample

**Option 1: Using Gradle**
```bash
./gradlew :sample:assembleDebug
./gradlew :sample:installDebug
```

**Option 2: Using Android Studio**
1. Open the project in Android Studio
2. Select the "sample" run configuration
3. Click Run

**Requirements:**
- Android device or emulator with Google Play Services
- Google account signed in
- At least one payment method in Google Pay

### Run iOS Sample

**Option 1: Using Xcode**
1. Open `/iosApp/iosApp.xcodeproj` in Xcode
2. Select a target device (physical device recommended for Apple Pay)
3. Click Run

**Option 2: Using IDE**
- Use the iOS run configuration in your Kotlin Multiplatform IDE

**Requirements:**
- Physical iOS device (Apple Pay not available in simulator)
- iOS 13.0 or later
- At least one card added to Apple Wallet
- Valid Apple Pay merchant ID configured

### Troubleshooting

#### Google Pay Issues

**"Google Pay is not available"**
- Ensure Google Play Services is installed and updated
- Sign in with a Google account
- Add at least one payment method to Google Pay
- Check that `INTERNET` permission is in AndroidManifest.xml

**"Payment failed"**
- Verify your gateway merchant ID is correct
- Check that your payment gateway is properly configured
- Use `TEST` environment for development
- Check backend integration

#### Apple Pay Issues

**"Apple Pay is not available"**
- Run on a physical device (not simulator)
- Ensure at least one card is in Wallet
- Verify Apple Pay capability is added in Xcode
- Check merchant ID matches PaymentConfig.kt

**"Payment failed"**
- Verify merchant ID is registered in Apple Developer Portal
- Check merchant identity certificate is configured
- Ensure merchant ID is selected in Xcode capabilities
- Test with a valid card in Wallet

**"No view controller available" error**
- The library automatically finds the root view controller
- Ensure your app has a valid keyWindow with a root view controller
- If using custom navigation, optionally set a custom provider:
  ```swift
  ApplePayViewControllerProviderHolder.shared.setProvider(YourCustomProvider())
  ```
- Clean and rebuild your Xcode project

### Sample App Structure

```
sample/
├── src/
│   ├── commonMain/
│   │   ├── App.kt                    # Main tabbed interface
│   │   ├── config/
│   │   │   └── PaymentConfig.kt      # Configuration values
│   │   └── screens/
│   │       └── ConfigScreen.kt       # Configuration viewer
│   ├── androidMain/
│   │   ├── PlatformPaymentScreen.android.kt
│   │   └── screens/
│   │       └── GooglePayScreen.kt    # Google Pay demo
│   └── iosMain/
│       ├── MainViewController.kt     # iOS entry point
│       ├── PlatformPaymentScreen.ios.kt
│       └── screens/
│           └── ApplePayScreen.kt     # Apple Pay demo
iosApp/
└── iosApp/
    ├── iOSApp.swift                  # App entry (no factory setup needed!)
    └── ContentView.swift             # Compose bridge

payment-mobile/ (library internals - no app setup required)
└── src/iosMain/kotlin/.../
    ├── KotlinNativeApplePayFactory.kt     # Pure Kotlin/Native PassKit integration
    └── ApplePayViewControllerProvider.kt  # Auto VC discovery
```

### Next Steps

After running the sample app:
1. Review the code in each screen to understand the integration
2. Check `PaymentConfig.kt` for configuration patterns
3. Examine platform-specific implementations
4. Test with different amounts and scenarios
5. Integrate the patterns into your own app

## Contributing

This is an internal library for KTTIPAY PTY LTD. For internal contributors, please follow the project's coding standards and submit pull requests for review.

## License

Copyright © 2024 KTTIPAY PTY LTD. All rights reserved.

---

**Built with Kotlin Multiplatform**

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
