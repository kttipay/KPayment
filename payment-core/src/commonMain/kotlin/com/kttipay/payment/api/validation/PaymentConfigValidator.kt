package com.kttipay.payment.api.validation

/**
 * Unified validator for payment configuration placeholder values.
 *
 * This validator checks if configuration values are still set to their default placeholder
 * values, indicating that the developer has not yet configured their actual credentials.
 *
 * Use this validator BEFORE creating config objects to provide friendly error messages
 * rather than generic validation errors from the config constructors.
 *
 * Example usage:
 * ```
 * val result = PaymentConfigValidator.validateGooglePayValues(
 *     merchantId = myMerchantId,
 *     merchantName = myMerchantName,
 *     gatewayMerchantId = myGatewayMerchantId
 * )
 * result.map { createGooglePayConfig() }
 * ```
 */
object PaymentConfigValidator {

    /**
     * Default placeholder values that indicate Google Pay is not configured.
     * Modify these values to update placeholder detection across all consumers.
     */
    object GooglePayPlaceholders {
        const val MERCHANT_ID = "YOUR_MERCHANT_ID_HERE"
        const val MERCHANT_NAME = "YOUR_MERCHANT_NAME_HERE"
        const val GATEWAY_MERCHANT_ID = "YOUR_GATEWAY_MERCHANT_ID_HERE"
    }

    /**
     * Default placeholder values that indicate Apple Pay is not configured.
     * Modify these values to update placeholder detection across all consumers.
     */
    object ApplePayPlaceholders {
        const val MERCHANT_ID = "merchant.com.yourcompany.yourapp"
        const val MERCHANT_VALIDATION_ENDPOINT = "https://your-backend.com/apple-pay/validate"
        const val MERCHANT_NAME = "YOUR_MERCHANT_NAME_HERE"
    }

    /**
     * Validates Google Pay configuration values against placeholder defaults.
     *
     * Call this BEFORE creating GooglePayConfig to get friendly error messages
     * that guide developers to configure their credentials.
     *
     * @param merchantId The Google Pay merchant ID to validate
     * @param merchantName The merchant name to validate
     * @param gatewayMerchantId The gateway merchant ID to validate
     * @return ConfigResult.Success with Unit if valid, ConfigResult.Failure with error messages if placeholders detected
     */
    fun validateGooglePayValues(
        merchantId: String,
        merchantName: String,
        gatewayMerchantId: String
    ): ConfigResult<Unit> {
        val errors = mutableListOf<String>()

        if (merchantId == GooglePayPlaceholders.MERCHANT_ID) {
            errors.add("Please configure Google Pay merchant ID in PaymentConfig.kt")
        }

        if (merchantName == GooglePayPlaceholders.MERCHANT_NAME) {
            errors.add("Please configure Google Pay merchant name in PaymentConfig.kt")
        }

        if (gatewayMerchantId == GooglePayPlaceholders.GATEWAY_MERCHANT_ID) {
            errors.add("Please configure Google Pay gateway merchant ID in PaymentConfig.kt")
        }

        return if (errors.isEmpty()) {
            ConfigResult.Success(Unit)
        } else {
            ConfigResult.Failure(
                providerName = "Google Pay",
                errors = errors
            )
        }
    }

    /**
     * Validates Apple Pay mobile configuration values against placeholder defaults.
     *
     * Call this BEFORE creating ApplePayMobileConfig to get friendly error messages
     * that guide developers to configure their credentials.
     *
     * @param merchantId The Apple Pay merchant ID to validate
     * @return ConfigResult.Success with Unit if valid, ConfigResult.Failure with error messages if placeholders detected
     */
    fun validateApplePayMobileValues(
        merchantId: String
    ): ConfigResult<Unit> {
        val errors = mutableListOf<String>()

        if (merchantId == ApplePayPlaceholders.MERCHANT_ID) {
            errors.add("Please configure Apple Pay merchant ID in PaymentConfig.kt")
        }

        return if (errors.isEmpty()) {
            ConfigResult.Success(Unit)
        } else {
            ConfigResult.Failure(
                providerName = "Apple Pay",
                errors = errors
            )
        }
    }

    /**
     * Validates Apple Pay web configuration values against placeholder defaults.
     *
     * Call this BEFORE creating ApplePayWebConfig to get friendly error messages
     * that guide developers to configure their credentials.
     *
     * @param merchantValidationEndpoint The merchant validation endpoint to validate
     * @return ConfigResult.Success with Unit if valid, ConfigResult.Failure with error messages if placeholders detected
     */
    fun validateApplePayWebValues(
        merchantValidationEndpoint: String
    ): ConfigResult<Unit> {
        val errors = mutableListOf<String>()

        if (merchantValidationEndpoint == ApplePayPlaceholders.MERCHANT_VALIDATION_ENDPOINT) {
            errors.add("Please configure Apple Pay merchant validation endpoint in PaymentConfig.kt")
        }

        return if (errors.isEmpty()) {
            ConfigResult.Success(Unit)
        } else {
            ConfigResult.Failure(
                providerName = "Apple Pay",
                errors = errors
            )
        }
    }
}
