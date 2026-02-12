package com.kttipay.payment.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.kttipay.payment.MobilePaymentManager
import com.kttipay.payment.api.PaymentEnvironment
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.config.GooglePayConfig
import com.kttipay.payment.api.config.MobilePaymentConfig
import com.kttipay.payment.capability.CapabilityStatus
import com.kttipay.payment.capability.PaymentCapabilities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * CompositionLocal for providing MobilePaymentManager to Compose UI.
 *
 * This allows Compose functions to access the payment manager without
 * explicit parameter passing.
 *
 * Usage:
 * ```kotlin
 * // Provide at root level
 * CompositionLocalProvider(LocalMobilePaymentManager provides paymentManager) {
 *     // Your app content
 * }
 *
 * // Access in composables
 * @Composable
 * fun MyPaymentScreen() {
 *     val paymentManager = LocalMobilePaymentManager.current
 *     // Use paymentManager
 * }
 * ```
 */
val LocalMobilePaymentManager = staticCompositionLocalOf<MobilePaymentManager> {
    error(
        "No MobilePaymentManager provided. Make sure to provide it using " +
            "CompositionLocalProvider(LocalMobilePaymentManager provides paymentManager) " +
            "at the root of your Compose tree."
    )
}

/**
 * Mock implementation of MobilePaymentManager for preview and testing purposes.
 * This implementation assumes all payment capabilities are ready and available.
 */
internal val MockPaymentManager = object : MobilePaymentManager {
    private val readyCapabilities = PaymentCapabilities(
        googlePay = CapabilityStatus.Ready,
        applePay = CapabilityStatus.Ready,
    )

    override val config: MobilePaymentConfig
        get() = MobilePaymentConfig(
            googlePay = GooglePayConfig(
                merchantId = "test_merchant_id",
                merchantName = "Test Merchant",
                countryCode = "US",
                currencyCode = "USD",
                gateway = "test_gateway",
                gatewayMerchantId = "test_gateway_merchant_id",
            ),
            environment = PaymentEnvironment.Development)

    override suspend fun checkCapabilities(): PaymentCapabilities = readyCapabilities

    override fun observeCapabilities(): Flow<PaymentCapabilities> = flowOf(readyCapabilities)

    override fun observeAvailability(provider: PaymentProvider): Flow<Boolean> = flowOf(true)
}
