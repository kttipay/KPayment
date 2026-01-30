package com.kttipay.payment.internal.validation

object AmountValidator {

    private val AMOUNT_REGEX = Regex("""^\d+(\.\d{1,2})?$""")

    fun validate(amount: String): ValidationResult {
        if (amount.isBlank()) {
            return ValidationResult.Error("Amount cannot be empty")
        }

        if (!AMOUNT_REGEX.matches(amount)) {
            return ValidationResult.Error(
                "Invalid amount format. Expected decimal number with max 2 decimal places (e.g., '10.00')"
            )
        }

        val numericAmount = amount.toDoubleOrNull() ?: return ValidationResult.Error(
            "Amount cannot be parsed as a number"
        )

        return ValidationResult.Valid(amount)
    }

    fun validateOrThrow(amount: String): String {
        return when (val result = validate(amount)) {
            is ValidationResult.Valid -> result.amount
            is ValidationResult.Error -> throw IllegalArgumentException(result.message)
        }
    }
}

sealed interface ValidationResult {
    data class Valid(val amount: String) : ValidationResult

    data class Error(val message: String) : ValidationResult
}
