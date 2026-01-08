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

[0.1.0]: https://github.com/kttipay/KPayment/releases/tag/v0.1.0
