package com.kttipay.payment.internal.googlepay

import kotlin.js.ExperimentalWasmJsInterop

/**
 * Minimal Google Pay client shared across modules to evaluate availability.
 * Payment-specific APIs live in platform modules.
 */
@OptIn(ExperimentalWasmJsInterop::class)
interface GooglePayWebClient {
    fun checkAvailability(
        onSuccess: (Boolean) -> Unit,
        onError: (Throwable) -> Unit
    )
}
