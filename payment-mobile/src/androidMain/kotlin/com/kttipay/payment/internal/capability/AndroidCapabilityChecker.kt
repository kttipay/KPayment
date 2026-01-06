package com.kttipay.payment.internal.capability

import android.content.Context
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.kttipay.payment.api.config.MobilePaymentConfig
import com.kttipay.payment.capability.CapabilityStatus
import com.kttipay.payment.internal.googlepay.GooglePayService
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Android implementation of CapabilityChecker.
 *
 * Uses Google Play Services to check Google Pay availability.
 * Apple Pay is not supported on Android.
 *
 * @param googlePayService Service for Google Pay operations
 * @param context Application context for Google Pay client creation
 */
internal class AndroidCapabilityChecker(
    private val googlePayService: GooglePayService,
    private val context: Context
) : CapabilityChecker {

    override suspend fun checkGooglePayAvailability(
        config: MobilePaymentConfig
    ): CapabilityStatus {
        if (config.googlePay == null) return CapabilityStatus.NotConfigured

        if (!googlePayService.isConfigured()) {
            return CapabilityStatus.Error("GooglePayService must be configured before checking availability")
        }

        val paymentsClient = googlePayService.createPaymentsClient(context)
        val request = IsReadyToPayRequest.fromJson(
            googlePayService.readyToPayRequest().toString()
        )

        return suspendCancellableCoroutine { continuation ->
            val task = paymentsClient.isReadyToPay(request)
            task.addOnCompleteListener { completedTask ->
                if (!continuation.isActive) return@addOnCompleteListener
                if (completedTask.isSuccessful) {
                    val ready = completedTask.result == true
                    val status = if (ready) {
                        CapabilityStatus.Ready
                    } else {
                        CapabilityStatus.Error("Google Pay unavailable on this device")
                    }
                    continuation.resume(status)
                } else {
                    continuation.resume(
                        CapabilityStatus.Error(
                            reason = "Google Pay availability check failed",
                            throwable = completedTask.exception
                        )
                    )
                }
            }
        }
    }

    override suspend fun checkApplePayAvailability(
        config: MobilePaymentConfig
    ): CapabilityStatus = CapabilityStatus.NotSupported
}
