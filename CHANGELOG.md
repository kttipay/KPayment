# Changelog

All notable changes to KPayment will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
