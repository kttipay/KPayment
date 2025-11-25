package com.kttipay.payment.api.logging

import com.kttipay.payment.internal.logging.KPaymentLogTree
import org.kimplify.cedar.logging.Cedar

/**
 * Callback interface for receiving log events from the KPayment library.
 *
 * Implement this interface to forward logs to your own logging system,
 * analytics platform, or custom destination.
 *
 * Example:
 * ```kotlin
 * KPaymentLogger.callback = object : KPaymentLogCallback {
 *     override fun onLog(event: LogEvent) {
 *         when (event.level) {
 *             LogLevel.ERROR -> MyLogger.error(event.tag, event.message, event.throwable)
 *             LogLevel.WARNING -> MyLogger.warn(event.tag, event.message)
 *             else -> MyLogger.debug(event.tag, event.message)
 *         }
 *     }
 * }
 * ```
 */
fun interface KPaymentLogCallback {
    /**
     * Called when a log event occurs in the KPayment library.
     *
     * @param event The log event containing level, tag, message, and optional throwable
     */
    fun onLog(event: LogEvent)
}

/**
 * Global logger configuration for the KPayment library.
 *
 * By default, all logging is disabled. Enable it by setting [enabled] to true:
 * ```kotlin
 * KPaymentLogger.enabled = true
 * ```
 *
 * To receive log events in your own logging system, provide a [callback]:
 * ```kotlin
 * KPaymentLogger.callback = object : KPaymentLogCallback {
 *     override fun onLog(event: LogEvent) {
 *         println("[${event.tag}] ${event.message}")
 *     }
 * }
 * ```
 */
object KPaymentLogger {
    private var _enabled: Boolean = false
    private var _callback: KPaymentLogCallback? = null
    private var logTree: KPaymentLogTree? = null
    private var isInitialized = false

    /**
     * Enable or disable logging for the KPayment library.
     *
     * When enabled, logs will be forwarded to the [callback] if one is set.
     * When disabled, all logging is suppressed.
     *
     * Default: false
     */
    var enabled: Boolean
        get() = _enabled
        set(value) {
            if (_enabled != value) {
                _enabled = value
                updateLoggingState()
            }
        }

    /**
     * Callback to receive log events from the KPayment library.
     *
     * Set this to forward logs to your own logging system.
     * If null, logs are discarded (but still suppressed if [enabled] is false).
     */
    var callback: KPaymentLogCallback?
        get() = _callback
        set(value) {
            _callback = value
            updateLoggingState()
        }

    /**
     * Initialize the logging system.
     * Called automatically on first access to [enabled] or [callback].
     */
    private fun initialize() {
        if (!isInitialized) {
            isInitialized = true
            updateLoggingState()
        }
    }

    /**
     * Updates the Cedar LogTree based on current enabled state and callback.
     */
    private fun updateLoggingState() {
        logTree?.let {
            Cedar.uproot(it)
            logTree = null
        }

        if (_enabled) {
            val tree = KPaymentLogTree(_callback)
            Cedar.plant(tree)
            logTree = tree
        }
    }

    init {
        initialize()
    }
}
