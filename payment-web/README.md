# Payment Web

A Kotlin Multiplatform library for integrating Google Pay and Apple Pay into web applications.

## Features

- **Google Pay** integration for web browsers
- **Apple Pay** integration for web browsers (Safari)
- **Kotlin/JS** and **Kotlin/Wasm** support
- **Jetpack Compose for Web** UI support
- **Dependency Injection** ready
- **Type-safe** browser API wrappers

## Setup

### 1. Add Dependency

```kotlin
// In your module's build.gradle.kts
dependencies {
    implementation(project(":payment-web"))
}
```

### 2. Initialize at App Startup (Recommended)

**Using Global Provider:**
```kotlin
// In your main.kt or app entry point
suspend fun onWebAppStart() {
    WebPaymentManagerProvider.initialize(
        WebPaymentConfig(
            environment = PaymentEnvironment.Production,
            googlePay = GooglePayConfig(
                merchantId = "YOUR_MERCHANT_ID",
                merchantName = "Your Business",
                gateway = "stripe",
                gatewayMerchantId = "YOUR_GATEWAY_ID"
            ),
            applePayWeb = ApplePayWebConfig(
                merchantId = "merchant.com.yourapp",
                merchantName = "Your Business"
            )
        )
    )
}
```

**Alternative - With Dependency Injection (Koin):**
```kotlin
val webModule = module {
    single<WebPaymentManager>(createdAtStart = true) {
        createWebPaymentManager().apply {
            runBlocking {
                initialize(
                    WebPaymentConfig(
                        environment = PaymentEnvironment.Production,
                        googlePay = GooglePayConfig(...),
                        applePayWeb = ApplePayWebConfig(...)
                    )
                )
            }
        }
    }
}
```

## Usage

### In ViewModels

```kotlin
class PaymentViewModel : ViewModel() {
    // Access global instance
    private val paymentManager = WebPaymentManagerProvider.instance

    val isApplePayAvailable = flow {
        emit(paymentManager.canUse(PaymentProvider.ApplePay))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun checkGooglePayAvailability() {
        if (paymentManager.canUse(PaymentProvider.GooglePay)) {
            // Google Pay is available
        }
    }
}
```

### In Composables

### Check Payment Availability

```kotlin
if (paymentManager.canUse(PaymentProvider.GooglePay)) {
    // Google Pay is available in this browser
}

if (paymentManager.canUse(PaymentProvider.ApplePay)) {
    // Apple Pay is available (Safari only)
}
```

### Payment Launchers

Payment launchers automatically use the global `WebPaymentManagerProvider` instance:

**Google Pay:**
```kotlin
@Composable
fun PaymentScreen() {
    val googlePayLauncher = rememberGooglePayWebLauncher { result ->
        when (result) {
            is GooglePayWebResult.Success -> handleSuccess(result.token)
            is GooglePayWebResult.Error -> handleError(result)
            is GooglePayWebResult.Cancelled -> handleCancellation()
        }
    }

    Button(onClick = {
        googlePayLauncher.launch(
            amount = Deci("10.00"),
            currency = "USD"
        )
    }) {
        Text("Pay with Google Pay")
    }
}
```

**Apple Pay:**
```kotlin
@Composable
fun PaymentScreen() {
    val applePayLauncher = rememberApplePayWebLauncher { result ->
        when (result) {
            is ApplePayWebLauncherResult.Success -> handleSuccess(result.token)
            is ApplePayWebLauncherResult.Error -> handleError(result)
            is ApplePayWebLauncherResult.Cancelled -> handleCancellation()
        }
    }

    Button(onClick = {
        applePayLauncher.launch(
            amount = Deci("10.00"),
            currency = "USD"
        )
    }) {
        Text("Pay with Apple Pay")
    }
}
```

### Check Payment Availability

```kotlin
// Access global instance (works in ViewModels, Composables, anywhere)
val manager = WebPaymentManagerProvider.instance

if (manager.canUse(PaymentProvider.GooglePay)) {
    // Google Pay is available
}

if (manager.canUse(PaymentProvider.ApplePay)) {
    // Apple Pay is available
}
```

### Get Active Configurations

```kotlin
val manager = WebPaymentManagerProvider.instance

// Access Google Pay configuration
val googlePayConfig = manager.googlePayConfig()

// Access Apple Pay configuration
val applePayConfig = manager.applePayConfig()
```

## API Reference

### WebPaymentManager

| Method | Description |
|--------|-------------|
| `initialize(config)` | Initialize with web payment configuration |
| `canUse(provider)` | Check if provider is available in browser |
| `currentCapabilities()` | Get current payment capabilities snapshot |
| `googlePayConfig()` | Get active Google Pay configuration |
| `applePayConfig()` | Get active Apple Pay configuration |
| `isInitialized()` | Check if manager has been initialized |

### PaymentProvider

- `PaymentProvider.GooglePay`
- `PaymentProvider.ApplePay`

### Browser Support

| Browser | Google Pay | Apple Pay |
|---------|------------|-----------|
| Chrome  | ✅         | ❌        |
| Firefox | ✅         | ❌        |
| Safari  | ✅         | ✅        |
| Edge    | ✅         | ❌        |

**Note:** Apple Pay Web requires:
- Safari browser on macOS or iOS
- Valid merchant identity certificate
- Domain verification

## Architecture

The library uses:
- **Dependency Injection** - Injectable `WebPaymentManager` interface
- **Clean Architecture** - Separation of concerns with internal/public APIs
- **Browser API Wrappers** - Type-safe Kotlin/JS external declarations
- **Compose Integration** - Composable launcher functions

## Migration from Singleton

**Before (v1.x):**
```kotlin
// Old singleton pattern
WebPaymentManager.initialize(config)
WebPaymentManager.canUse(provider)
```

**After (v2.x):**
```kotlin
// New DI pattern
val paymentManager = createWebPaymentManager() // or inject
paymentManager.initialize(config)
paymentManager.canUse(provider)
```

## License

Internal library for KTTIPAY PTY LTD
