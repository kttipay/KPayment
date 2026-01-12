package com.kttipay.payment.internal.googlepay

import com.kttipay.payment.api.PaymentErrorReason
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult
import com.kttipay.payment.internal.googlepay.launcher.GooglePayWebLauncher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GooglePayWebLauncherTest {

    @Test
    fun `launch sets isProcessing to true before calling payment client`() = runTest {
        var wasProcessingWhenClientCalled = false
        lateinit var launcher: GooglePayWebLauncher
        val client = createMockClient { _, _ ->
            wasProcessingWhenClientCalled = launcher.isProcessing.value
        }
        launcher = GooglePayWebLauncher(client) {}

        launcher.launch("10.00")

        assertTrue(wasProcessingWhenClientCalled)
    }

    @Test
    fun `launch sets isProcessing to false after successful payment`() = runTest {
        val client = createMockClient { _, callback ->
            callback(GooglePayWebResult.Success("token_123"))
        }
        val launcher = GooglePayWebLauncher(client) {}

        launcher.launch("10.00")

        assertFalse(launcher.isProcessing.value)
    }

    @Test
    fun `launch sets isProcessing to false after cancelled payment`() = runTest {
        val client = createMockClient { _, callback ->
            callback(GooglePayWebResult.Cancelled)
        }
        val launcher = GooglePayWebLauncher(client) {}

        launcher.launch("10.00")

        assertFalse(launcher.isProcessing.value)
    }

    @Test
    fun `launch sets isProcessing to false after payment error`() = runTest {
        val client = createMockClient { _, callback ->
            callback(GooglePayWebResult.Error(GooglePayWebErrorCode.UNKNOWN))
        }
        val launcher = GooglePayWebLauncher(client) {}

        launcher.launch("10.00")

        assertFalse(launcher.isProcessing.value)
    }

    @Test
    fun `launch returns AlreadyInProgress error when called while processing`() = runTest {
        var receivedResults = mutableListOf<PaymentResult>()
        val client = createMockClient { _, _ -> }
        val launcher = GooglePayWebLauncher(client) { result ->
            receivedResults.add(result)
        }

        launcher.launch("10.00")
        launcher.launch("20.00")

        assertEquals(1, receivedResults.size)
        val error = receivedResults[0]
        assertTrue(error is PaymentResult.Error)
        assertEquals(PaymentErrorReason.AlreadyInProgress, error.reason)
        assertEquals("A payment is already in progress", error.message)
    }

    @Test
    fun `launch does not call payment client when already processing`() = runTest {
        var clientCallCount = 0
        val client = createMockClient { _, _ ->
            clientCallCount++
        }
        val launcher = GooglePayWebLauncher(client) {}

        launcher.launch("10.00")
        launcher.launch("20.00")

        assertEquals(1, clientCallCount)
    }

    @Test
    fun `launch invokes onResult callback with Success`() = runTest {
        val client = createMockClient { _, callback ->
            callback(GooglePayWebResult.Success("test_token"))
        }
        var receivedResult: PaymentResult? = null
        val launcher = GooglePayWebLauncher(client) { result ->
            receivedResult = result
        }

        launcher.launch("10.00")

        assertTrue(receivedResult is PaymentResult.Success)
        val successResult = receivedResult as PaymentResult.Success
        assertEquals("test_token", successResult.token)
        assertEquals(PaymentProvider.GooglePay, successResult.provider)
    }

    @Test
    fun `launch invokes onResult callback with Cancelled`() = runTest {
        val client = createMockClient { _, callback ->
            callback(GooglePayWebResult.Cancelled)
        }
        var receivedResult: PaymentResult? = null
        val launcher = GooglePayWebLauncher(client) { result ->
            receivedResult = result
        }

        launcher.launch("10.00")

        assertTrue(receivedResult is PaymentResult.Cancelled)
        val cancelledResult = receivedResult as PaymentResult.Cancelled
        assertEquals(PaymentProvider.GooglePay, cancelledResult.provider)
    }

    @Test
    fun `launch invokes onResult callback with Error`() = runTest {
        val client = createMockClient { _, callback ->
            callback(
                GooglePayWebResult.Error(
                    errorCode = GooglePayWebErrorCode.LOAD_PAYMENT_DATA_FAILED,
                    additionalMessage = "Network failure"
                )
            )
        }
        var receivedResult: PaymentResult? = null
        val launcher = GooglePayWebLauncher(client) { result ->
            receivedResult = result
        }

        launcher.launch("10.00")

        assertTrue(receivedResult is PaymentResult.Error)
        val errorResult = receivedResult as PaymentResult.Error
        assertEquals(PaymentErrorReason.InternalError, errorResult.reason)
        assertEquals("Network failure", errorResult.message)
        assertEquals(PaymentProvider.GooglePay, errorResult.provider)
    }

    @Test
    fun `provider property returns GooglePay`() {
        val client = createMockClient { _, _ -> }
        val launcher = GooglePayWebLauncher(client) {}

        assertEquals(PaymentProvider.GooglePay, launcher.provider)
    }

    @Test
    fun `isProcessing observable emits current state`() = runTest {
        val client = createMockClient { _, _ -> }
        val launcher = GooglePayWebLauncher(client) {}

        val initialState = launcher.isProcessing.first()

        assertFalse(initialState)
    }

    @Test
    fun `launch can be called again after previous payment completes`() = runTest {
        val client = createMockClient { _, callback ->
            callback(GooglePayWebResult.Success("token"))
        }
        var callCount = 0
        val launcher = GooglePayWebLauncher(client) { callCount++ }

        launcher.launch("10.00")
        launcher.launch("20.00")

        assertEquals(2, callCount)
        assertFalse(launcher.isProcessing.value)
    }

    private fun createMockClient(
        onRequestPayment: (String, (GooglePayWebResult) -> Unit) -> Unit
    ): GooglePayPaymentClient {
        return object : GooglePayPaymentClient {
            override fun requestPayment(amount: String, onResult: (GooglePayWebResult) -> Unit) {
                onRequestPayment(amount, onResult)
            }
        }
    }
}
