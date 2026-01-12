package com.kttipay.payment.internal.googlepay

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Composable
fun rememberGooglePayLauncher(
    onResult: (PaymentResult) -> Unit
): PaymentLauncher {
    val context = LocalContext.current
    val paymentsClient = remember(context) { GooglePayEnvironment.createPaymentsClient(context) }

    val processingState = remember { MutableStateFlow(false) }
    val currentOnResult by rememberUpdatedState(onResult)

    val launcher = rememberLauncherForActivityResult(contract = TaskResultContracts.GetPaymentDataResult()) { taskResult ->
            processingState.update { false }
            val provider = PaymentProvider.GooglePay
            when (taskResult.status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val token = taskResult.result?.toJson()
                    if (!token.isNullOrEmpty()) {
                        currentOnResult(PaymentResult.Success(provider = provider, token = token))
                    } else {
                        currentOnResult(
                            PaymentResult.Error(
                                provider = provider,
                                reason = PaymentErrorReason.Unknown,
                                message = "Empty Google Pay token"
                            )
                        )
                    }
                }

                CommonStatusCodes.CANCELED -> {
                    currentOnResult(PaymentResult.Cancelled(provider))
                }

                else -> {
                    currentOnResult(
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
        AndroidPaymentLauncher(
            launcher = launcher,
            paymentsClient = paymentsClient,
            onResult = { currentOnResult(it) },
            processingState = processingState
        )
    }
}

private class AndroidPaymentLauncher(
    private val launcher: ManagedActivityResultLauncher<Task<PaymentData>, ApiTaskResult<PaymentData>>,
    private val paymentsClient: PaymentsClient,
    private val onResult: (PaymentResult) -> Unit,
    private val processingState: MutableStateFlow<Boolean>,
) : PaymentLauncher {
    override val provider: PaymentProvider = PaymentProvider.GooglePay
    override val isProcessing: StateFlow<Boolean> = processingState.asStateFlow()

    override fun launch(amount: String) {
        if (!processingState.compareAndSet(expect = false, update = true)) {
            onResult(
                PaymentResult.Error(
                    provider = provider,
                    reason = PaymentErrorReason.AlreadyInProgress,
                    message = "A payment is already in progress"
                )
            )
            return
        }

        when (val validationResult = AmountValidator.validate(amount)) {
            is ValidationResult.Error -> {
                processingState.update { false }
                onResult(
                    PaymentResult.Error(
                        provider = provider,
                        reason = PaymentErrorReason.DeveloperError,
                        message = validationResult.message
                    )
                )
            }

            is ValidationResult.Valid -> {
                val requestJson = GooglePayEnvironment.paymentDataRequest(validationResult.amount)
                val request = PaymentDataRequest.fromJson(requestJson.toString())
                paymentsClient.loadPaymentData(request).addOnCompleteListener {
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
