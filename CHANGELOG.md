# Changelog

All notable changes to KPayment will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.3.0] — 2026-04-22

### Breaking
- `GooglePayConfig` now takes a single `gateway: GatewayConfig` instead of `gateway: String` + `gatewayMerchantId: String`. The old shape was incorrect for Stripe (missing `stripe:publishableKey` / `stripe:version`) and caused `OR_BIBED_06` errors at runtime. ([#7](https://github.com/kttipay/KPayment/issues/7))
- `GooglePayWebConfig.googlePayGateway` / `.googlePayGatewayMerchantId` replaced by `.gateway: GatewayConfig`.
- `PaymentConfigValidator.validateGooglePayValues(...)` signature drops the `gatewayMerchantId` parameter. Placeholder constant `GooglePayPlaceholders.GATEWAY_MERCHANT_ID` removed.

### Added
- `GatewayConfig` sealed class in `payment-core`:
  - `GatewayConfig.Stripe(publishableKey, apiVersion, stripeAccountId?)` — type-safe Stripe support.
  - `GatewayConfig.Custom(gatewayName, gatewayMerchantId?, additionalParameters)` — escape hatch for any other gateway (FatZebra, Braintree, Adyen, Checkout.com, ...).
- Regression tests on Android and Web locking the tokenization JSON shape per gateway.

### Fixed
- Google Pay on Web + Stripe now works end-to-end. Resolves [#7](https://github.com/kttipay/KPayment/issues/7).

### Migration

```kotlin
// Before (0.2.x)
GooglePayConfig(
    merchantId = "...",
    merchantName = "...",
    gateway = "stripe",
    gatewayMerchantId = "pk_live_...",   // This was never valid for Stripe.
    ...
)

// After (0.3.0) — Stripe
GooglePayConfig(
    merchantId = "...",
    merchantName = "...",
    gateway = GatewayConfig.Stripe(publishableKey = "pk_live_..."),
    ...
)

// After (0.3.0) — FatZebra / Adyen / any other standard-shape gateway
GooglePayConfig(
    merchantId = "...",
    merchantName = "...",
    gateway = GatewayConfig.Custom(
        gatewayName = "fatzebra",
        gatewayMerchantId = "<your merchant id>",
    ),
    ...
)

// After (0.3.0) — Braintree / anything with gateway-specific keys
GooglePayConfig(
    ...,
    gateway = GatewayConfig.Custom(
        gatewayName = "braintree",
        additionalParameters = mapOf(
            "braintree:apiVersion" to "v1",
            "braintree:clientKey" to "production_xyz_...",
        ),
    ),
)
```

## [0.1.0] - 2025-01-06

### Added
- Initial release of KPayment library
- Google Pay support for Android and Web (JS/WASM)
- Apple Pay support for iOS and Web (Safari)
- Kotlin Multiplatform architecture (Android, iOS, Web)
- Compose Multiplatform UI components (PaymentButton)
- Payment capability detection via Flow
- Type-safe configuration models
- Serializable payment tokens
- KPaymentLogger for optional debugging
- Comprehensive documentation and samples

### Changed
- Payment amounts use String type for decimal values (e.g., "10.00")

### Fixed
- Thread safety in iOS ApplePayFactory
- Amount validation to prevent invalid payment requests
- Request deduplication to prevent duplicate payment charges

### Security
- All publications signed for Maven Central distribution

### Deprecated
- `GooglePayEnvironment` - Use `GooglePayService` with dependency injection instead (will be removed in v0.2.0)

## [Unreleased]

## [0.1.1] - 2025-01-30

### Added
- Compose Preview support for `PaymentButton` on Android and iOS using `LocalInspectionMode`
- `AmountValidator` integration in Apple Pay launcher (iOS), matching Android behavior

### Changed
- Migrated build system to Gradle 9 with updated Kotlin Multiplatform configuration
- Refactored `sampleMobile` module into `app` and `shared` submodules for Gradle 9 compatibility
- `AmountValidator` now allows zero-amount payments (`"0.00"`) for card verification and token enrollment flows
- Web sample app no longer blocks payment when only one provider is configured

### Fixed
- `PaymentButton` no longer crashes in `@Preview` due to platform-native Google Pay / Apple Pay button dependencies
- Web sample `hasConfigError` logic that prevented Google Pay launcher from being created
- Apple Pay launcher now validates amounts before forwarding to the native SDK

**Full Changelog**: https://github.com/kttipay/KPayment/compare/v0.1.0...v0.1.1

[0.1.1]: https://github.com/kttipay/KPayment/releases/tag/v0.1.1
[0.1.0]: https://github.com/kttipay/KPayment/releases/tag/v0.1.0
