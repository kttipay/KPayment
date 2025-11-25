package com.kttipay.payment.internal.logging

import com.kttipay.payment.api.logging.KPaymentLogger
import org.kimplify.cedar.logging.Cedar
import kotlin.jvm.JvmInline

/**
 * Internal logging facade for KPayment library.
 *
 * This facade ensures complete log isolation - when KPaymentLogger.enabled = false,
 * NO logs will escape to user-planted Cedar trees.
 *
 * All logging calls are inlined and short-circuited when logging is disabled,
 * ensuring zero performance overhead.
 *
 * Usage:
 * ```kotlin
 * InternalLogger.tag("MyComponent").d("Debug message")
 * InternalLogger.tag("MyComponent").w("Warning", throwable)
 * InternalLogger.tag("MyComponent").e("Error", throwable)
 * ```
 */
object KPaymentLogger {

    /**
     * Creates a tagged logger instance.
     * Returns a logger that will respect KPaymentLogger.enabled state.
     */
    inline fun tag(tag: String): TaggedLogger = TaggedLogger(tag)

    /**
     * Log a debug message without a tag.
     */
    inline fun d(message: String) {
        if (KPaymentLogger.enabled) {
            Cedar.d(message)
        }
    }

    /**
     * Log a warning message without a tag.
     */
    inline fun w(message: String, throwable: Throwable? = null) {
        if (KPaymentLogger.enabled) {
            if (throwable != null) {
                Cedar.w(message, throwable)
            } else {
                Cedar.w(message)
            }
        }
    }

    /**
     * Log an error message without a tag.
     */
    inline fun e(message: String, throwable: Throwable? = null) {
        if (KPaymentLogger.enabled) {
            if (throwable != null) {
                Cedar.e(message, throwable)
            } else {
                Cedar.e(message)
            }
        }
    }

    /**
     * Log an info message without a tag.
     */
    inline fun i(message: String) {
        if (KPaymentLogger.enabled) {
            Cedar.i(message)
        }
    }
}

/**
 * Tagged logger that respects KPaymentLogger.enabled state.
 */
@JvmInline
value class TaggedLogger(@PublishedApi internal val tag: String) {

    /**
     * Log a debug message.
     */
    inline fun d(message: String) {
        if (KPaymentLogger.enabled) {
            Cedar.tag(tag).d(message)
        }
    }

    /**
     * Log a warning message.
     */
    inline fun w(message: String, throwable: Throwable? = null) {
        if (KPaymentLogger.enabled) {
            if (throwable != null) {
                Cedar.tag(tag).w(message, throwable)
            } else {
                Cedar.tag(tag).w(message)
            }
        }
    }

    /**
     * Log an error message.
     */
    inline fun e(message: String, throwable: Throwable? = null) {
        if (KPaymentLogger.enabled) {
            if (throwable != null) {
                Cedar.tag(tag).e(message, throwable)
            } else {
                Cedar.tag(tag).e(message)
            }
        }
    }

    /**
     * Log an info message.
     */
    inline fun i(message: String) {
        if (KPaymentLogger.enabled) {
            Cedar.tag(tag).i(message)
        }
    }

    /**
     * Log a verbose message.
     */
    inline fun v(message: String) {
        if (KPaymentLogger.enabled) {
            Cedar.tag(tag).v(message)
        }
    }
}
