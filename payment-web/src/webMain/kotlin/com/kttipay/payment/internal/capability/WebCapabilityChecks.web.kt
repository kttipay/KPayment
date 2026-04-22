package com.kttipay.payment.internal.capability

import com.kttipay.payment.api.config.ApplePayWebConfig
import com.kttipay.payment.api.config.GooglePayWebConfig
import com.kttipay.payment.capability.CapabilityStatus
import com.kttipay.payment.internal.googlepay.GooglePayWebClient
import com.kttipay.payment.internal.googlepay.GooglePayWebClientImpl
import com.kttipay.payment.internal.logging.KPaymentLogger
import com.kttipay.payment.internal.utils.ScriptLoader
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.JsFun
import kotlin.coroutines.resume
import kotlin.js.ExperimentalWasmJsInterop

private const val TAG = "WebCapabilityChecks"

/**
 * Safely checks ApplePaySession.canMakePayments() with full JS-level error handling.
 *
 * Returns:
 * - 1 if ApplePaySession exists and canMakePayments() returns true
 * - 0 if ApplePaySession is undefined or canMakePayments() returns false
 * - -1 if canMakePayments() threw an error (e.g., SecurityError on non-HTTPS)
 */
@OptIn(ExperimentalWasmJsInterop::class)
@JsFun(
    """
    function() {
        try {
            if (typeof ApplePaySession === 'undefined') {
                console.log('[KPayment] ApplePaySession is undefined');
                return 0;
            }
            var result = ApplePaySession.canMakePayments();
            console.log('[KPayment] ApplePaySession.canMakePayments() =', result);
            return result ? 1 : 0;
        } catch(e) {
            console.warn('[KPayment] ApplePaySession.canMakePayments() threw:', e);
            return -1;
        }
    }
    """
)
private external fun safeCanMakePaymentsCode(): Int

internal actual suspend fun checkApplePayAvailability(
    config: ApplePayWebConfig
): CapabilityStatus {
    if (config.enableJsSdk) {
        val sdkResult = ScriptLoader.loadApplePaySdkScript()
        KPaymentLogger.tag(TAG).d("Apple Pay SDK load result: ${sdkResult.isSuccess}")
        sdkResult.onFailure { error ->
            KPaymentLogger.tag(TAG).w("Apple Pay SDK load failed, falling back to native API", error)
        }
    }

    return runCatching { safeCanMakePaymentsCode() }
        .map { code ->
            KPaymentLogger.tag(TAG).d("Apple Pay canMakePayments code: $code")
            when (code) {
                1 -> CapabilityStatus.Ready
                0 -> CapabilityStatus.Error("Apple Pay unavailable for ${config.base.merchantName}")
                else -> CapabilityStatus.Error("Apple Pay canMakePayments() threw an error (non-HTTPS?)")
            }
        }
        .getOrElse { error ->
            KPaymentLogger.tag(TAG).e("Apple Pay availability check failed", error)
            CapabilityStatus.Error("Apple Pay availability check failed: ${error.message}", error)
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
