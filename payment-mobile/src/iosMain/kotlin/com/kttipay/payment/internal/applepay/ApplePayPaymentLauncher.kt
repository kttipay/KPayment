package com.kttipay.payment.internal.applepay

import com.kttipay.payment.api.PaymentErrorReason
import com.kttipay.payment.api.PaymentLauncher
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult
import com.kttipay.payment.api.config.ApplePayMobileConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class ApplePayPaymentLauncher(
    private val config: ApplePayMobileConfig,
    private val onResult: (PaymentResult) -> Unit,
) : PaymentLauncher {
    override val provider: PaymentProvider = PaymentProvider.ApplePay
    private val _isProcessing = MutableStateFlow(false)
    override val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val factory: ApplePayFactory by lazy {
        IosApplePayManager.getFactory()
    }

    override fun launch(amount: String) {
        if (!_isProcessing.compareAndSet(expect = false, update = true)) {
            onResult(
                PaymentResult.Error(
                    provider = provider,
                    reason = PaymentErrorReason.AlreadyInProgress,
                    message = "A payment is already in progress"
                )
            )
            return
        }

        val baseConfig = config.base
        factory.startPayment(
            ApplePayRequest(
                merchantId = config.merchantId,
                countryCode = baseConfig.countryCode,
                currencyCode = baseConfig.currencyCode,
                supportedNetworks = baseConfig.supportedNetworks,
                merchantCapabilities = baseConfig.merchantCapabilities,
                summaryItems = listOf(
                    SummaryItem(
                        label = baseConfig.merchantName,
                        amount = amount,
                        isFinal = true
                    )
                )
            ),
            onResult = { result ->
                _isProcessing.value = false
                onResult(result.toPaymentResult())
            }
        )
    }
}

private fun ApplePayResult.toPaymentResult(): PaymentResult {
    return when (this) {
        is ApplePayResult.Success -> PaymentResult.Success(
            provider = PaymentProvider.ApplePay,
            token = tokenJson
        )

        ApplePayResult.Cancelled -> PaymentResult.Cancelled(
            provider = PaymentProvider.ApplePay
        )

        is ApplePayResult.Failure -> PaymentResult.Error(
            provider = PaymentProvider.ApplePay,
            reason = errorCode.toPaymentErrorReason(),
            message = message
        )
    }
}

private fun ApplePayErrorCode.toPaymentErrorReason(): PaymentErrorReason {
    return when (this) {
        ApplePayErrorCode.PRESENT_FAILED -> PaymentErrorReason.NotAvailable
        ApplePayErrorCode.TOKEN_EXTRACTION_FAILED -> PaymentErrorReason.InternalError
        ApplePayErrorCode.AUTHORIZATION_FAILED -> PaymentErrorReason.DeveloperError
        ApplePayErrorCode.UNKNOWN -> PaymentErrorReason.Unknown
    }
}
