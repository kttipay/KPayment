package com.kttipay.payment.internal.logging

import com.kttipay.payment.api.logging.KPaymentLogCallback
import com.kttipay.payment.api.logging.LogEvent
import com.kttipay.payment.api.logging.LogLevel
import com.kttipay.payment.api.logging.currentTimeMillis
import org.kimplify.cedar.logging.LogPriority
import org.kimplify.cedar.logging.LogTree

/**
 * Custom Cedar LogTree that intercepts logs from the KPayment library
 * and forwards them to the user-provided callback.
 *
 * This tree is planted by [com.kttipay.payment.api.logging.KPaymentLogger]
 * when logging is enabled.
 */
internal class KPaymentLogTree(
    private val callback: KPaymentLogCallback?
) : LogTree {

    override fun log(
        priority: LogPriority,
        tag: String,
        message: String,
        throwable: Throwable?
    ) {
        forwardLog(
            level = priority.toLevel(),
            tag = tag,
            message = message,
            throwable = throwable
        )
    }

    private fun forwardLog(level: LogLevel, tag: String?, message: String, throwable: Throwable?) {
        callback?.onLog(
            LogEvent(
                level = level,
                tag = tag,
                message = message,
                throwable = throwable,
                timestamp = currentTimeMillis()
            )
        )
    }

    private fun LogPriority.toLevel(): LogLevel {
        return when (this) {
            LogPriority.VERBOSE -> LogLevel.VERBOSE
            LogPriority.DEBUG -> LogLevel.DEBUG
            LogPriority.INFO -> LogLevel.INFO
            LogPriority.WARNING -> LogLevel.WARNING
            LogPriority.ERROR -> LogLevel.ERROR
        }
    }
}
