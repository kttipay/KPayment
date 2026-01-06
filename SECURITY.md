# Security Guidelines

This document outlines security best practices for using the KPayment library in your applications.

## Payment Token Security

### Never Store Payment Tokens

Payment tokens returned by KPayment are sensitive and should never be stored on the device or in local storage.

**Do:**
- Send tokens directly to your backend server immediately after receiving them
- Process tokens server-side only
- Use secure HTTPS connections for all token transmission

**Don't:**
- Store tokens in SharedPreferences, UserDefaults, or local databases
- Log tokens in production builds
- Send tokens to third-party analytics services
- Cache tokens for later use

```kotlin
when (val result = paymentResult) {
    is PaymentResult.Success -> {
        val token = result.token
        // Immediately send to backend
        sendTokenToBackend(token)
    }
    // ...
}
```

### Token Transmission

Always use HTTPS when transmitting payment tokens to your backend:

```kotlin
suspend fun sendTokenToBackend(token: String) {
    val response = httpClient.post("https://your-backend.com/process-payment") {
        contentType(ContentType.Application.Json)
        body = PaymentRequest(token = token)
    }
}
```

## Merchant ID Protection

### Keep Merchant IDs Secure

Merchant IDs are sensitive configuration values that should be protected:

**Do:**
- Store merchant IDs in secure configuration files
- Use environment-specific configurations (development vs production)
- Never commit merchant IDs to public repositories
- Use build-time configuration injection for sensitive values

**Don't:**
- Hardcode merchant IDs in source code
- Commit merchant IDs to version control
- Expose merchant IDs in client-side code if possible
- Share merchant IDs publicly

### Configuration Management

```kotlin
// Use build config or environment variables
val merchantId = BuildConfig.MERCHANT_ID
// or
val merchantId = System.getenv("MERCHANT_ID") ?: throw IllegalStateException("Merchant ID not configured")

val config = GooglePayConfig(
    merchantId = merchantId,
    merchantName = "Your Store",
    gateway = "stripe",
    gatewayMerchantId = gatewayMerchantId
)
```

## Network Security

### HTTPS Requirements

All network communication related to payments must use HTTPS:

- Payment token transmission to backend
- Apple Pay merchant validation endpoints
- Google Pay API calls (handled automatically by the library)

**Never use HTTP** for payment-related endpoints.

### Certificate Pinning

Consider implementing certificate pinning for payment endpoints:

```kotlin
val httpClient = HttpClient {
    engine {
        // Configure certificate pinning for payment endpoints
    }
}
```

## Environment Configuration

### Use Appropriate Environments

Always use the correct environment for your deployment:

- **Development**: Use `PaymentEnvironment.Development` for testing and development
- **Production**: Use `PaymentEnvironment.Production` for live applications

```kotlin
val config = MobilePaymentConfig(
    environment = if (BuildConfig.DEBUG) {
        PaymentEnvironment.Development
    } else {
        PaymentEnvironment.Production
    },
    googlePay = googlePayConfig
)
```

### Environment-Specific Credentials

Use different merchant IDs and credentials for development and production:

```kotlin
val merchantId = when (environment) {
    PaymentEnvironment.Development -> "test_merchant_id"
    PaymentEnvironment.Production -> "production_merchant_id"
}
```

## Input Validation

### Validate Payment Amounts

Always validate payment amounts before initiating payment:

```kotlin
fun processPayment(amount: String) {
    val validationResult = AmountValidator.validate(amount)
    when (validationResult) {
        is ValidationResult.Valid -> {
            launchPayment(amount)
        }
        is ValidationResult.Error -> {
            showError(validationResult.message)
        }
    }
}
```

### Validate Configuration

The library validates configuration at initialization, but you should also validate user inputs:

```kotlin
fun createConfig(merchantId: String, merchantName: String): GooglePayConfig {
    require(merchantId.isNotBlank()) { "Merchant ID cannot be blank" }
    require(merchantName.isNotBlank()) { "Merchant name cannot be blank" }
    
    return GooglePayConfig(
        merchantId = merchantId,
        merchantName = merchantName,
        gateway = "stripe",
        gatewayMerchantId = gatewayMerchantId
    )
}
```

## Apple Pay Web Security

### Domain Validation

For Apple Pay on Web, ensure proper domain validation:

1. Register your domain with Apple Developer account
2. Host the domain verification file at the correct location
3. Implement merchant validation endpoint securely
4. Use HTTPS for all Apple Pay endpoints

### Merchant Validation Endpoint

Your merchant validation endpoint must:

- Be accessible via HTTPS
- Validate requests appropriately
- Return valid merchant session data
- Handle errors securely

```kotlin
val applePayWeb = ApplePayWebConfig(
    base = ApplePayBaseConfig(merchantName = "Your Store"),
    merchantValidationEndpoint = "https://your-backend.com/apple-pay/validate",
    baseUrl = "https://your-domain.com",
    domain = "your-domain.com"
)
```

## Logging and Debugging

### Disable Logging in Production

KPayment logging is disabled by default. If you enable it for debugging, ensure it's disabled in production:

```kotlin
if (BuildConfig.DEBUG) {
    KPaymentLogger.enabled = true
    KPaymentLogger.callback = object : KPaymentLogCallback {
        override fun onLog(event: LogEvent) {
            Log.d(event.tag, event.message)
        }
    }
} else {
    KPaymentLogger.enabled = false
}
```

### Never Log Sensitive Data

Never log payment tokens, merchant IDs, or other sensitive information:

```kotlin
// Don't do this:
Log.d("Payment", "Token: $token")

// Do this instead:
Log.d("Payment", "Payment successful")
```

## Error Handling

### Don't Expose Internal Errors

When handling errors, don't expose internal implementation details to users:

```kotlin
is PaymentResult.Error -> {
    when (result.reason) {
        PaymentErrorReason.DeveloperError -> {
            // Log internally, show generic message to user
            Log.e("Payment", "Configuration error: ${result.message}")
            showError("Payment configuration error. Please contact support.")
        }
        else -> {
            showError("Payment failed. Please try again.")
        }
    }
}
```

## Backend Integration

### Server-Side Token Processing

Always process payment tokens on your backend server:

1. Receive token from client
2. Validate token format
3. Send token to payment gateway
4. Process payment server-side
5. Return result to client

**Never process payments client-side.**

### Token Validation

Validate tokens on your backend before processing:

```kotlin
// Backend validation example (pseudo-code)
fun validateToken(token: String): Boolean {
    // Validate token format
    // Verify token signature if applicable
    // Check token expiration
    return isValid
}
```

## Compliance

### PCI DSS Compliance

If you store, process, or transmit cardholder data, ensure PCI DSS compliance:

- Use payment tokens instead of raw card data
- Process payments through PCI-compliant payment gateways
- Never store cardholder data
- Use secure communication channels

### Data Privacy

Respect user privacy:

- Only request payment information when necessary
- Clearly communicate how payment data is used
- Comply with applicable data protection regulations (GDPR, CCPA, etc.)
- Implement proper data retention policies

## Reporting Security Issues

If you discover a security vulnerability in KPayment, please report it responsibly:

1. **Do not** create a public GitHub issue
2. Email security concerns to: kosta0212@gmail.com
3. Include details about the vulnerability
4. Allow time for the issue to be addressed before public disclosure

## Additional Resources

- [Google Pay Security Best Practices](https://developers.google.com/pay/api/web/guides/best-practices)
- [Apple Pay Security Guide](https://developer.apple.com/documentation/passkit/apple_pay)
- [PCI DSS Compliance Guide](https://www.pcisecuritystandards.org/)
- [OWASP Mobile Security](https://owasp.org/www-project-mobile-security/)

