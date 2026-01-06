package com.kttipay.payment.internal.googlepay

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.contract.ApiTaskResult
import com.google.android.gms.wallet.contract.TaskResultContracts
import com.kttipay.payment.api.PaymentErrorReason
import com.kttipay.payment.api.PaymentLauncher
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult
import com.kttipay.payment.internal.validation.AmountValidator
import com.kttipay.payment.internal.validation.ValidationResult

@Composable
fun rememberGooglePayLauncher(
    onResult: (PaymentResult) -> Unit
): PaymentLauncher {
    val context = LocalContext.current
    val paymentsClient = remember(context) { GooglePayEnvironment.createPaymentsClient(context) }

    val launcher = rememberLauncherForActivityResult(
        contract = TaskResultContracts.GetPaymentDataResult()
    ) { taskResult ->
        val provider = PaymentProvider.GooglePay
        when (taskResult.status.statusCode) {
            CommonStatusCodes.SUCCESS -> {
                val token = taskResult.result?.toJson()
                if (!token.isNullOrEmpty()) {
                    onResult(PaymentResult.Success(provider = provider, token = token))
                } else {
                    onResult(
                        PaymentResult.Error(
                            provider = provider,
                            reason = PaymentErrorReason.Unknown,
                            message = "Empty Google Pay token"
                        )
                    )
                }
            }

            CommonStatusCodes.CANCELED -> {
                onResult(PaymentResult.Cancelled(provider))
            }
            else -> {
                onResult(
                    PaymentResult.Error(
                        provider = provider,
                        reason = statusToPaymentErrorReason(taskResult.status.statusCode),
                        message = taskResult.status.statusMessage
                    )
                )
            }
        }
    }

    return remember(launcher, paymentsClient) {
        AndroidPaymentLauncher(launcher, paymentsClient)
    }
}

private class AndroidPaymentLauncher(
    private val launcher: ManagedActivityResultLauncher<Task<PaymentData>, ApiTaskResult<PaymentData>>,
    private val paymentsClient: PaymentsClient,
) : PaymentLauncher {
    override val provider: PaymentProvider = PaymentProvider.GooglePay
    private var isProcessing: Boolean = false

    override fun launch(amount: String) {
        if (isProcessing) {
            return
        }
        isProcessing = true

        when (val validationResult = AmountValidator.validate(amount)) {
            is ValidationResult.Error -> {
                isProcessing = false
                throw IllegalArgumentException(validationResult.message)
            }
            is ValidationResult.Valid -> {
                val requestJson = GooglePayEnvironment.paymentDataRequest(validationResult.amount)
                val request = PaymentDataRequest.fromJson(requestJson.toString())
                val task = paymentsClient.loadPaymentData(request)
                task.addOnCompleteListener {
                    isProcessing = false
                    launcher.launch(it)
                }
            }
        }
    }
}

private fun statusToPaymentErrorReason(statusCode: Int): PaymentErrorReason {
    return when (statusCode) {
        CommonStatusCodes.TIMEOUT -> PaymentErrorReason.Timeout
        CommonStatusCodes.API_NOT_CONNECTED -> PaymentErrorReason.ApiNotConnected
        CommonStatusCodes.CONNECTION_SUSPENDED_DURING_CALL -> PaymentErrorReason.ConnectionSuspendedDuringCall
        CommonStatusCodes.DEVELOPER_ERROR -> PaymentErrorReason.DeveloperError
        CommonStatusCodes.INTERNAL_ERROR -> PaymentErrorReason.InternalError
        CommonStatusCodes.INTERRUPTED -> PaymentErrorReason.Interrupted
        CommonStatusCodes.NETWORK_ERROR -> PaymentErrorReason.NetworkError
        CommonStatusCodes.SIGN_IN_REQUIRED -> PaymentErrorReason.SignInRequired
        CommonStatusCodes.ERROR -> PaymentErrorReason.Unknown
        else -> PaymentErrorReason.Unknown
    }
}
