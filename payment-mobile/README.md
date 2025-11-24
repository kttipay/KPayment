# Payment Mobile

A Kotlin Multiplatform library for integrating Google Pay and Apple Pay into your mobile applications.

## Features

- **Google Pay** integration for Android
- **Apple Pay** integration for iOS
- **Kotlin Multiplatform** support (Android & iOS)
- **Jetpack Compose** UI components
- **Dependency Injection** ready (Koin)
- **Reactive** capability checking with Kotlin Flows

## Setup

### 1. Add Dependency

```kotlin
// In your module's build.gradle.kts
dependencies {
    implementation(project(":payment-mobile"))
}
```

### 2. Register with Koin

The library is automatically registered in your platform modules:

**Android** (`PlatformModule.kt`):
```kotlin
single<MobilePaymentManager> { createMobilePaymentManager(androidApplication()) }
```

**iOS** (`PlatformModule.kt`):
```kotlin
single<MobilePaymentManager> { createMobilePaymentManager() }
```

### 3. Provide CompositionLocal (Compose UI)

In your root composable:

```kotlin
@Composable
fun App() {
    val paymentManager: MobilePaymentManager = koinInject()

    CompositionLocalProvider(
        LocalMobilePaymentManager provides paymentManager
    ) {
        // Your app content
    }
}
```

## Usage

### Initialize Payment Manager

Inject and initialize in your ViewModel:

```kotlin
@KoinViewModel
class PaymentViewModel(
    private val mobilePaymentManager: MobilePaymentManager
) : ViewModel() {

    init {
        viewModelScope.launch {
            mobilePaymentManager.initialize {
                environment(PaymentEnvironment.Production)

                googlePay(GooglePayConfig(
                    merchantId = "YOUR_MERCHANT_ID",
                    merchantName = "Your Business",
                    gateway = "stripe",
                    gatewayMerchantId = "YOUR_GATEWAY_ID"
                ))

                applePay(ApplePayMobileConfig(
                    merchantId = "merchant.com.yourapp",
                    base = ApplePayBaseConfig(merchantName = "Your Business")
                ))
            }
        }
    }
}
```

### Check Payment Availability

```kotlin
val isReady = mobilePaymentManager
    .observeAvailability(PaymentProvider.GooglePay)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)
```

### Use Payment Buttons (Compose)

```kotlin
@Composable
fun PaymentScreen() {
    val launcher = rememberNativePaymentLauncher { result ->
        when (result) {
            is PaymentResult.Success -> handleSuccess(result)
            is PaymentResult.Error -> handleError(result)
            is PaymentResult.Cancelled -> handleCancellation()
        }
    }

    PaymentButton(
        theme = NativePaymentTheme.Dark,
        type = NativePaymentType.Pay,
        onClick = { launcher.launch(amount = Deci("10.00")) }
    )
}
```

### Check Capabilities

```kotlin
val capabilities = mobilePaymentManager.currentCapabilities()

when (capabilities.googlePay) {
    is CapabilityStatus.Ready -> // Google Pay is available
    is CapabilityStatus.NotConfigured -> // Not configured
    is CapabilityStatus.Error -> // Error occurred
    else -> // Not supported on this platform
}
```

## API Reference

### MobilePaymentManager

| Method | Description |
|--------|-------------|
| `initialize(config)` | Initialize with payment configuration |
| `observeAvailability(provider)` | Observe payment provider availability (Flow) |
| `canUse(provider)` | Check if provider is ready (Boolean) |
| `currentCapabilities()` | Get current payment capabilities snapshot |
| `refreshCapabilities()` | Re-check all provider capabilities |

### PaymentProvider

- `PaymentProvider.GooglePay`
- `PaymentProvider.ApplePay`

### CapabilityStatus

- `Ready` - Provider is available and ready
- `NotConfigured` - Provider not configured
- `NotSupported` - Provider not supported on this platform
- `Error(reason, throwable)` - Error occurred

## Platform Support

| Platform | Google Pay | Apple Pay |
|----------|------------|-----------|
| Android  | ✅         | ❌        |
| iOS      | ❌         | ✅        |

## Architecture

The library uses:
- **Dependency Injection** - All components injectable via Koin
- **Clean Architecture** - Separation of concerns with internal/public APIs
- **Reactive Streams** - Kotlin Flow for reactive updates
- **Compose Integration** - First-class Compose support

## License

Internal library for KTTIPAY PTY LTD
