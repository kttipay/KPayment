package com.kttipay.payment.api.logging

/**
 * Log severity levels.
 */
enum class LogLevel {
    VERBOSE,
    DEBUG,
    INFO,
    WARNING,
    ERROR
}

/**
 * Represents a single log event from the KPayment library.
 *
 * @property level The severity level of the log
 * @property tag Optional tag identifying the source component
 * @property message The log message
 * @property throwable Optional exception associated with this log
 * @property timestamp Platform-specific timestamp when the log occurred
 */
data class LogEvent(
    val level: LogLevel,
    val tag: String?,
    val message: String,
    val throwable: Throwable?,
    val timestamp: Long
)

/**
 * Gets the current platform timestamp in milliseconds.
 * Implementation varies by platform.
 */
expect fun currentTimeMillis(): Long
