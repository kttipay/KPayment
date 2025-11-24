# Payment Core

Common interfaces, models, and types shared across all payment platform implementations (mobile, web).

## Overview

This module provides the foundational API surface that all payment implementations must adhere to, ensuring consistency across platforms while allowing platform-specific optimizations.

## Module Structure

```
payment-core/
├── api/                        # Public API
│   ├── PaymentManager.kt      # Common manager interface
│   ├── PaymentLauncher.kt     # Payment launcher interface
│   ├── PaymentProvider.kt     # Provider enum (GooglePay, ApplePay)
│   ├── PaymentResult.kt       # Result types (Success, Error, Cancelled)
│   ├── PaymentEnvironment.kt  # Environment enum (Production, Development)
│   └── config/                # Configuration models
├── capability/                # Capability system
│   ├── PaymentCapabilities.kt # Capabilities data class
│   └── CapabilityStatus.kt    # Status types
├── model/                     # Payment tokens
│   ├── ApplePayToken.kt
│   └── GooglePayToken.kt
└── internal/                  # Internal utilities
    └── config/
        └── ApiConstants.kt
```

## Core Interfaces

### PaymentManager

The base interface that all platform payment managers must implement:

```kotlin
interface PaymentManager {
    suspend fun initialize(config: PlatformPaymentConfig): PaymentCapabilities
    fun canUse(provider: PaymentProvider): Boolean
    fun currentCapabilities(): PaymentCapabilities
    fun isInitialized(): Boolean
}
```

**Implementations:**
- `MobilePaymentManager` (payment-mobile) - Android & iOS
- `WebPaymentManager` (payment-web) - Browser/Web

### PaymentLauncher

Interface for launching payment flows:

```kotlin
interface PaymentLauncher {
    val provider: PaymentProvider
    fun launch(amount: Deci)
}
```

## Shared Models

### PaymentProvider

```kotlin
enum class PaymentProvider {
    GooglePay,
    ApplePay
}
```

### PaymentResult

```kotlin
sealed interface PaymentResult {
    data class Success(val provider: PaymentProvider, val token: String) : PaymentResult
    data class Cancelled(val provider: PaymentProvider) : PaymentResult
    data class Error(val provider: PaymentProvider, val reason: PaymentErrorReason, val message: String?) : PaymentResult
}
```

### PaymentCapabilities

```kotlin
data class PaymentCapabilities(
    val googlePay: CapabilityStatus,
    val applePay: CapabilityStatus
) {
    fun canPayWith(provider: PaymentProvider): Boolean
}
```

### CapabilityStatus

```kotlin
sealed interface CapabilityStatus {
    data object NotConfigured : CapabilityStatus
    data object NotSupported : CapabilityStatus
    data object Ready : CapabilityStatus
    data class Error(val reason: String, val throwable: Throwable? = null) : CapabilityStatus
}
```

## Configuration Hierarchy

### Platform-Agnostic Config

```kotlin
interface PlatformPaymentConfig {
    val environment: PaymentEnvironment
    val googlePay: GooglePayConfig?
}
```

### Platform-Specific Configs

**Mobile:**
```kotlin
data class MobilePaymentConfig(
    override val environment: PaymentEnvironment,
    override val googlePay: GooglePayConfig?,
    val applePay: ApplePayMobileConfig?
) : PlatformPaymentConfig
```

**Web:**
```kotlin
data class WebPaymentConfig(
    override val environment: PaymentEnvironment,
    override val googlePay: GooglePayConfig?,
    val applePayWeb: ApplePayWebConfig?
) : PlatformPaymentConfig
```

### Shared Configurations

**GooglePayConfig** (used by both mobile and web):
```kotlin
data class GooglePayConfig(
    val merchantId: String,
    val merchantName: String,
    val gateway: String,
    val gatewayMerchantId: String
)
```

**ApplePayBaseConfig** (shared properties):
```kotlin
data class ApplePayBaseConfig(
    val merchantName: String,
    val countryCode: String = "AU",
    val currencyCode: String = "AUD",
    val supportedNetworks: List<String> = listOf("visa", "masterCard", "amex")
)
```

## Payment Tokens

### ApplePayToken

```kotlin
@Serializable
data class ApplePayToken(
    val paymentData: PaymentData,
    val paymentMethod: PaymentMethod,
    val transactionIdentifier: String
)
```

### GooglePayToken

```kotlin
@Serializable
data class GooglePayToken(
    val signature: String,
    val protocolVersion: String,
    val signedMessage: String
)
```

## Usage by Platform Modules

### payment-mobile

```kotlin
interface MobilePaymentManager : PaymentManager {
    // Extends with mobile-specific features:
    val capabilitiesFlow: StateFlow<PaymentCapabilities>
    fun observeAvailability(provider: PaymentProvider): Flow<Boolean>
    suspend fun initialize(buildConfig: PaymentConfigBuilder.() -> Unit): PaymentCapabilities
    fun reset()
    suspend fun reinitialize(config: MobilePaymentConfig): PaymentCapabilities
}
```

### payment-web

```kotlin
interface WebPaymentManager : PaymentManager {
    // Extends with web-specific features:
    fun applePayConfig(): ApplePayWebConfig
    fun googlePayConfig(): GooglePayWebConfig
}
```

## Dependencies

- `kotlinx.serialization` - For token serialization
- `kotlinx.coroutines` - For suspend functions
- `Cedar` - For internal logging

## Platform Support

| Platform | GooglePay | ApplePay |
|----------|-----------|----------|
| Android  | ✅        | ❌       |
| iOS      | ❌        | ✅       |
| Web      | ✅        | ✅*      |

*Apple Pay on web requires Safari browser

## License

Internal library for KTTIPAY PTY LTD
