package com.kttipay.payment.api.validation

/**
 * Result type for payment configuration validation.
 *
 * This sealed interface provides a type-safe alternative to returning Pair<Config?, String?>,
 * ensuring compile-time guarantees about success and failure states.
 */
sealed interface ConfigResult<out T> {
    /**
     * Configuration was successfully validated.
     *
     * @property config The successfully validated configuration object
     */
    data class Success<T>(val config: T) : ConfigResult<T>

    /**
     * Configuration validation failed due to errors.
     *
     * @property providerName Name of the payment provider (e.g., "Google Pay", "Apple Pay")
     * @property errors List of validation error messages
     */
    data class Failure(
        val providerName: String,
        val errors: List<String>
    ) : ConfigResult<Nothing> {
        /**
         * Formatted error message for display to users.
         * Combines provider name with all error messages.
         */
        val message: String
            get() = "$providerName:\n${errors.joinToString("\n")}"
    }
}

/**
 * Transforms the successful result using the provided transform function.
 * Failures are passed through unchanged.
 *
 * @param transform Function to apply to the config if successful
 * @return Transformed result
 */
inline fun <T, R> ConfigResult<T>.map(transform: (T) -> R): ConfigResult<R> =
    when (this) {
        is ConfigResult.Success -> ConfigResult.Success(transform(config))
        is ConfigResult.Failure -> this
    }

/**
 * Extracts the configuration if successful, or null if failed.
 * Useful for providing nullable configs to constructors.
 *
 * @return Configuration object or null
 */
fun <T> ConfigResult<T>.getOrNull(): T? =
    when (this) {
        is ConfigResult.Success -> config
        is ConfigResult.Failure -> null
    }

/**
 * Extracts the error message if failed, or null if successful.
 * Useful for error display logic.
 *
 * @return Error message or null
 */
fun <T> ConfigResult<T>.errorOrNull(): String? =
    when (this) {
        is ConfigResult.Success -> null
        is ConfigResult.Failure -> message
    }

/**
 * Executes the given action if the result is successful.
 * Returns the original result for chaining.
 *
 * @param action Action to execute with the config
 * @return Original result (for chaining)
 */
inline fun <T> ConfigResult<T>.onSuccess(action: (T) -> Unit): ConfigResult<T> {
    if (this is ConfigResult.Success) action(config)
    return this
}

/**
 * Executes the given action if the result is a failure.
 * Returns the original result for chaining.
 *
 * @param action Action to execute with provider name and errors
 * @return Original result (for chaining)
 */
inline fun <T> ConfigResult<T>.onFailure(action: (String, List<String>) -> Unit): ConfigResult<T> {
    if (this is ConfigResult.Failure) action(providerName, errors)
    return this
}

/**
 * Combines error messages from multiple configuration results.
 * Only includes failures; successful results are ignored.
 *
 * @return List of formatted error messages
 */
fun <T> List<ConfigResult<T>>.combineErrors(): List<String> =
    filterIsInstance<ConfigResult.Failure>().map { it.message }
