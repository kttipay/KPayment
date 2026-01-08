package com.kttipay.payment

import com.kttipay.payment.api.PaymentEnvironment
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.config.GooglePayConfig
import com.kttipay.payment.api.config.MobilePaymentConfig
import com.kttipay.payment.api.config.PlatformPaymentConfig
import com.kttipay.payment.capability.CapabilityStatus
import com.kttipay.payment.strategy.CapabilityCheckStrategy
import com.kttipay.payment.strategy.ConfigAccessor
import com.kttipay.payment.strategy.PlatformSetupStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PaymentManagerImplTest {
    @Test
    fun `awaitCapabilities waits for initial check and returns capabilities`() = runTest {
        val config = createTestConfig()
        val manager = createManager(
            config = config,
            capabilityCheckStrategy = createMockCapabilityStrategy(
                googlePayStatus = CapabilityStatus.Ready,
                applePayStatus = CapabilityStatus.NotConfigured
            ),
            scope = this@runTest
        )

        val capabilities = manager.awaitCapabilities()

        assertEquals(CapabilityStatus.Ready, capabilities.googlePay)
        assertEquals(CapabilityStatus.NotConfigured, capabilities.applePay)
    }

    @Test
    fun `awaitCapabilities returns cached capabilities on subsequent calls`() = runTest {
        val config = createTestConfig()
        var callCount = 0
        val manager = createManager(
            config = config,
            capabilityCheckStrategy = object : CapabilityCheckStrategy {
                override suspend fun checkGooglePayAvailability(config: PlatformPaymentConfig): CapabilityStatus {
                    callCount++
                    return CapabilityStatus.Ready
                }

                override suspend fun checkApplePayAvailability(config: PlatformPaymentConfig) =
                    CapabilityStatus.NotConfigured
            },
            scope = this@runTest
        )

        manager.awaitCapabilities()
        manager.awaitCapabilities()
        manager.awaitCapabilities()

        assertEquals(1, callCount)
    }

    @Test
    fun `capabilitiesFlow emits capabilities after refresh`() = runTest {
        val config = createTestConfig()
        val manager = createManager(
            config = config,
            capabilityCheckStrategy = createMockCapabilityStrategy(
                googlePayStatus = CapabilityStatus.Ready,
                applePayStatus = CapabilityStatus.NotConfigured
            ),
            scope = this@runTest
        )

        manager.refreshCapabilities()
        val capabilities = manager.capabilitiesFlow.value

        assertEquals(CapabilityStatus.Ready, capabilities.googlePay)
        assertEquals(CapabilityStatus.NotConfigured, capabilities.applePay)
    }

    @Test
    fun `refreshCapabilities updates capabilities`() = runTest {
        val config = createTestConfig()
        var googlePayStatus: CapabilityStatus = CapabilityStatus.NotConfigured
        val manager = createManager(
            config = config,
            capabilityCheckStrategy = object : CapabilityCheckStrategy {
                override suspend fun checkGooglePayAvailability(config: PlatformPaymentConfig) = googlePayStatus

                override suspend fun checkApplePayAvailability(config: PlatformPaymentConfig) =
                    CapabilityStatus.NotConfigured
            },
            scope = this@runTest
        )

        val initialCapabilities = manager.refreshCapabilities()
        assertEquals(CapabilityStatus.NotConfigured, initialCapabilities.googlePay)

        googlePayStatus = CapabilityStatus.Ready
        val updatedCapabilities = manager.refreshCapabilities()
        assertEquals(CapabilityStatus.Ready, updatedCapabilities.googlePay)
    }

    @Test
    fun `observeAvailability emits false when provider not ready`() = runTest {
        val config = createTestConfig()
        val manager = createManager(
            config = config,
            capabilityCheckStrategy = createMockCapabilityStrategy(
                googlePayStatus = CapabilityStatus.NotConfigured,
                applePayStatus = CapabilityStatus.NotConfigured
            ),
            scope = this@runTest
        )

        manager.refreshCapabilities()

        val isAvailable = manager.observeAvailability(PaymentProvider.GooglePay).first()
        assertFalse(isAvailable)
    }

    @Test
    fun `observeAvailability emits true when provider ready`() = runTest {
        val config = createTestConfig()
        val manager = createManager(
            config = config,
            capabilityCheckStrategy = createMockCapabilityStrategy(
                googlePayStatus = CapabilityStatus.Ready,
                applePayStatus = CapabilityStatus.Ready
            ),
            scope = this@runTest
        )

        manager.refreshCapabilities()

        val googlePayAvailable = manager.observeAvailability(PaymentProvider.GooglePay).first()
        val applePayAvailable = manager.observeAvailability(PaymentProvider.ApplePay).first()

        assertTrue(googlePayAvailable)
        assertTrue(applePayAvailable)
    }

    @Test
    fun `config property returns provided config`() = runTest {
        val config = createTestConfig()
        val manager = createManager(config = config, scope = this@runTest)

        assertEquals(config, manager.config)
    }

    private fun createTestConfig(): MobilePaymentConfig {
        return MobilePaymentConfig(
            environment = PaymentEnvironment.Development,
            googlePay = GooglePayConfig(
                merchantId = "test_merchant",
                merchantName = "Test Merchant",
                gateway = "test_gateway",
                gatewayMerchantId = "test_gateway_id"
            )
        )
    }

    private fun createManager(
        config: PlatformPaymentConfig = createTestConfig(),
        capabilityCheckStrategy: CapabilityCheckStrategy = createMockCapabilityStrategy(),
        platformSetupStrategy: PlatformSetupStrategy = createMockPlatformSetupStrategy(),
        configAccessor: ConfigAccessor = createMockConfigAccessor(),
        scope: CoroutineScope
    ): PaymentManagerImpl<PlatformPaymentConfig> {
        return PaymentManagerImpl(
            config = config,
            capabilityCheckStrategy = capabilityCheckStrategy,
            platformSetupStrategy = platformSetupStrategy,
            configAccessor = configAccessor,
            scope = scope
        )
    }

    private fun createMockCapabilityStrategy(
        googlePayStatus: CapabilityStatus = CapabilityStatus.NotConfigured,
        applePayStatus: CapabilityStatus = CapabilityStatus.NotConfigured
    ): CapabilityCheckStrategy {
        return object : CapabilityCheckStrategy {
            override suspend fun checkGooglePayAvailability(config: PlatformPaymentConfig) = googlePayStatus

            override suspend fun checkApplePayAvailability(config: PlatformPaymentConfig) = applePayStatus
        }
    }

    private fun createMockPlatformSetupStrategy(): PlatformSetupStrategy {
        return object : PlatformSetupStrategy {
            override fun setupPlatformPayments(config: PlatformPaymentConfig) {
            }
        }
    }

    private fun createMockConfigAccessor(): ConfigAccessor {
        return object : ConfigAccessor {
            override fun getApplePayConfig(config: PlatformPaymentConfig) = null

            override fun getGooglePayConfig(config: PlatformPaymentConfig) = config.googlePay
        }
    }
}
