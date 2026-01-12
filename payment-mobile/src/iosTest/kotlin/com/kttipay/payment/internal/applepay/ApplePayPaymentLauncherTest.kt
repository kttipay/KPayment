package com.kttipay.payment.internal.applepay

import com.kttipay.payment.api.PaymentErrorReason
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult
import com.kttipay.payment.api.config.ApplePayBaseConfig
import com.kttipay.payment.api.config.ApplePayMobileConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ApplePayPaymentLauncherTest {

    private lateinit var mockFactory: MockApplePayFactory
    private lateinit var receivedResults: MutableList<PaymentResult>

    @BeforeTest
    fun setup() {
        mockFactory = MockApplePayFactory()
        receivedResults = mutableListOf()
        IosApplePayManager.setCustomFactory(mockFactory)
    }

    @Test
    fun `provider property returns ApplePay`() {
        val launcher = createLauncher()
        assertEquals(PaymentProvider.ApplePay, launcher.provider)
    }

    @Test
    fun `isProcessing initial state is false`() = runTest {
        val launcher = createLauncher()
        val initialState = launcher.isProcessing.first()
        assertFalse(initialState)
    }

    @Test
    fun `launch sets isProcessing to true before calling factory`() = runTest {
        var wasProcessingWhenFactoryCalled = false
        val launcher = createLauncher()
        mockFactory.onStartPayment = { _, _ ->
            wasProcessingWhenFactoryCalled = launcher.isProcessing.value
        }

        launcher.launch("10.00")

        assertTrue(wasProcessingWhenFactoryCalled)
    }

    @Test
    fun `launch sets isProcessing to false after successful payment`() = runTest {
        val launcher = createLauncher()
        mockFactory.onStartPayment = { _, callback ->
            callback(ApplePayResult.Success("token", null))
        }

        launcher.launch("10.00")

        assertFalse(launcher.isProcessing.value)
    }

    @Test
    fun `launch sets isProcessing to false after cancelled payment`() = runTest {
        val launcher = createLauncher()
        mockFactory.onStartPayment = { _, callback ->
            callback(ApplePayResult.Cancelled)
        }

        launcher.launch("10.00")

        assertFalse(launcher.isProcessing.value)
    }

    @Test
    fun `launch sets isProcessing to false after payment error`() = runTest {
        val launcher = createLauncher()
        mockFactory.onStartPayment = { _, callback ->
            callback(ApplePayResult.Failure(ApplePayErrorCode.UNKNOWN))
        }

        launcher.launch("10.00")

        assertFalse(launcher.isProcessing.value)
    }

    @Test
    fun `launch returns AlreadyInProgress error when called while processing`() = runTest {
        val launcher = createLauncher { result ->
            receivedResults.add(result)
        }

        launcher.launch("10.00")
        launcher.launch("20.00")

        assertEquals(1, receivedResults.size)
        val error = receivedResults[0]
        assertTrue(error is PaymentResult.Error)
        assertEquals(PaymentErrorReason.AlreadyInProgress, error.reason)
        assertEquals("A payment is already in progress", error.message)
        assertEquals(PaymentProvider.ApplePay, error.provider)
    }

    @Test
    fun `launch does not call factory when already processing`() = runTest {
        var factoryCallCount = 0
        val launcher = createLauncher()
        mockFactory.onStartPayment = { _, _ ->
            factoryCallCount++
        }

        launcher.launch("10.00")
        launcher.launch("20.00")

        assertEquals(1, factoryCallCount)
    }

    @Test
    fun `launch invokes onResult callback with Success`() = runTest {
        val launcher = createLauncher { result ->
            receivedResults.add(result)
        }
        mockFactory.onStartPayment = { _, callback ->
            callback(ApplePayResult.Success("test_token", "txn_123"))
        }

        launcher.launch("10.00")

        assertEquals(1, receivedResults.size)
        val result = receivedResults[0]
        assertTrue(result is PaymentResult.Success)
        assertEquals("test_token", result.token)
        assertEquals(PaymentProvider.ApplePay, result.provider)
    }

    @Test
    fun `launch invokes onResult callback with Cancelled`() = runTest {
        val launcher = createLauncher { result ->
            receivedResults.add(result)
        }
        mockFactory.onStartPayment = { _, callback ->
            callback(ApplePayResult.Cancelled)
        }

        launcher.launch("10.00")

        assertEquals(1, receivedResults.size)
        val result = receivedResults[0]
        assertTrue(result is PaymentResult.Cancelled)
        assertEquals(PaymentProvider.ApplePay, result.provider)
    }

    @Test
    fun `launch invokes onResult callback with Error`() = runTest {
        val launcher = createLauncher { result ->
            receivedResults.add(result)
        }
        mockFactory.onStartPayment = { _, callback ->
            callback(
                ApplePayResult.Failure(
                    errorCode = ApplePayErrorCode.PRESENT_FAILED,
                    additionalMessage = "Payment sheet failed"
                )
            )
        }

        launcher.launch("10.00")

        assertEquals(1, receivedResults.size)
        val result = receivedResults[0]
        assertTrue(result is PaymentResult.Error)
        assertEquals(PaymentErrorReason.NotAvailable, result.reason)
        assertEquals("Payment sheet failed", result.message)
        assertEquals(PaymentProvider.ApplePay, result.provider)
    }

    @Test
    fun `launch can be called again after previous payment completes`() = runTest {
        val launcher = createLauncher { result ->
            receivedResults.add(result)
        }
        mockFactory.onStartPayment = { _, callback ->
            callback(ApplePayResult.Success("token", null))
        }

        launcher.launch("10.00")
        launcher.launch("20.00")

        assertEquals(2, receivedResults.size)
        assertFalse(launcher.isProcessing.value)
    }

    private fun createLauncher(
        onResult: (PaymentResult) -> Unit = {}
    ): ApplePayPaymentLauncher {
        val config = ApplePayMobileConfig(
            merchantId = "merchant.test",
            base = ApplePayBaseConfig(merchantName = "Test Merchant")
        )
        return ApplePayPaymentLauncher(config, onResult)
    }

    private class MockApplePayFactory : ApplePayFactory {
        var onStartPayment: ((ApplePayRequest, (ApplePayResult) -> Unit) -> Unit)? = null

        override fun applePayStatus(): ApplePayStatus {
            return ApplePayStatus(canMakePayments = true, canSetupCards = true)
        }

        override fun startPayment(
            request: ApplePayRequest,
            onResult: (ApplePayResult) -> Unit
        ) {
            onStartPayment?.invoke(request, onResult)
        }

        override fun presentSetupFlow(onFinished: (Boolean) -> Unit) {
            onFinished(true)
        }
    }
}
