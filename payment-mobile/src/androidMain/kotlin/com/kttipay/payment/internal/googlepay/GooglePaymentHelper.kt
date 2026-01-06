package com.kttipay.payment.internal.googlepay

import android.content.Context
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentsClient
import com.kttipay.payment.internal.logging.KPaymentLogger
import com.kttipay.payment.ui.NativePaymentHelper
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class GooglePaymentHelper(
    context: Context
) : NativePaymentHelper {
    private val paymentsClient: PaymentsClient = GooglePayEnvironment.createPaymentsClient(context)

    override suspend fun isReadyToPay(): Boolean = suspendCancellableCoroutine { continuation ->
        val request = IsReadyToPayRequest.fromJson(
            GooglePayEnvironment.readyToPayRequest().toString()
        )
        val task = paymentsClient.isReadyToPay(request)
        task.addOnCompleteListener { completedTask ->
            if (!continuation.isActive) return@addOnCompleteListener

            runCatching {
                val result = completedTask.getResult(ApiException::class.java)
                continuation.resume(result == true)
            }.onFailure { error ->
                if (continuation.isActive) {
                    continuation.resume(false)
                }
                KPaymentLogger.tag("GooglePaymentHelper").e("isReadyToPay failed", error)
            }
        }
    }
}
