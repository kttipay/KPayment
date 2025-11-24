package com.kttipay.payment.internal.applepay

import com.kttipay.payment.api.PaymentErrorReason
import com.kttipay.payment.api.PaymentLauncher
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult
import com.kttipay.payment.api.config.ApplePayMobileConfig
import org.kimplify.deci.Deci

/**
 * Payment launcher for Apple Pay on iOS.
 * Uses KotlinNativeApplePayFactory by default, or a custom factory if set via IosApplePayManager.
 */
internal class ApplePayPaymentLauncher(
    private val config: ApplePayMobileConfig,
    private val onResult: (PaymentResult) -> Unit,
) : PaymentLauncher {
    override val provider: PaymentProvider = PaymentProvider.ApplePay

    private val factory: ApplePayFactory by lazy {
        IosApplePayManager.getFactory()
    }

    override fun launch(amount: Deci) {
        val baseConfig = config.base
        factory.startPayment(
            ApplePayRequest(
                merchantId = config.merchantId,
                countryCode = baseConfig.countryCode,
                currencyCode = baseConfig.currencyCode,
                supportedNetworks = baseConfig.supportedNetworks,
                merchantCapabilities3DS = baseConfig.merchantCapabilities.contains("supports3DS"),
                summaryItems = listOf(
                    SummaryItem(
                        label = baseConfig.merchantName,
                        amount = amount
                    )
                )
            ),
            onResult = { result ->
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
            reason = PaymentErrorReason.Unknown,
            message = message
        )
    }
}
