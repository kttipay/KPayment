package com.kttipay.payment.internal.capability

import com.kttipay.payment.internal.applepay.ApplePayAvailability
import com.kttipay.payment.api.config.ApplePayWebConfig
import com.kttipay.payment.api.config.GooglePayWebConfig
import com.kttipay.payment.capability.CapabilityStatus
import com.kttipay.payment.internal.googlepay.GooglePayWebClient
import com.kttipay.payment.internal.googlepay.GooglePayWebClientImpl
import com.kttipay.payment.internal.utils.ScriptLoader
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal actual fun checkApplePayAvailability(
    config: ApplePayWebConfig
): CapabilityStatus {
    return runCatching { ApplePayAvailability.canMakePayments() }
        .map { available ->
            if (available) {
                CapabilityStatus.Ready
            } else {
                CapabilityStatus.Error("Apple Pay unavailable for ${config.base.merchantName}")
            }
        }
        .getOrElse { error ->
            CapabilityStatus.Error("Apple Pay availability check failed", error)
        }
}

internal actual suspend fun checkGooglePayAvailability(
    config: GooglePayWebConfig
): CapabilityStatus {
    ScriptLoader.loadGooglePayScript()
    return suspendCancellableCoroutine { continuation ->
        val client: GooglePayWebClient = GooglePayWebClientImpl(config)
        client.checkAvailability(
            onSuccess = { available ->
                val status = if (available) {
                    CapabilityStatus.Ready
                } else {
                    CapabilityStatus.Error("Google Pay unavailable on this browser")
                }
                continuation.resume(status)
            },
            onError = { error ->
                continuation.resume(
                    CapabilityStatus.Error(
                        "Google Pay availability check failed",
                        error
                    )
                )
            }
        )
    }
}
