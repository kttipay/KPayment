package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kttipay.payment.WebPaymentManager
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.config.WebPaymentConfig
import com.kttipay.payment.capability.CapabilityStatus
import com.kttipay.payment.capability.PaymentCapabilities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

@Composable
actual fun rememberWebPaymentManager(config: WebPaymentConfig): WebPaymentManager {
    return remember(config) {
        object : WebPaymentManager {
            private val notSupportedCapabilities = PaymentCapabilities(
                googlePay = CapabilityStatus.NotSupported,
                applePay = CapabilityStatus.NotSupported
            )

            override val config: WebPaymentConfig = config

            override val capabilitiesFlow: StateFlow<PaymentCapabilities> =
                MutableStateFlow(notSupportedCapabilities)

            override suspend fun awaitCapabilities(): PaymentCapabilities {
                return notSupportedCapabilities
            }

            override suspend fun refreshCapabilities(): PaymentCapabilities {
                return notSupportedCapabilities
            }

            override fun observeAvailability(provider: PaymentProvider): Flow<Boolean> {
                return flowOf(false)
            }
        }
    }
}
