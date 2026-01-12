package com.kttipay.payment.internal.googlepay

import com.kttipay.payment.api.PaymentErrorReason
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GooglePayWebResultTest {

    @Test
    fun `toPaymentResult converts Success correctly`() {
        val result = GooglePayWebResult.Success(token = "test_token_123")

        val paymentResult = result.toPaymentResult()

        assertTrue(paymentResult is PaymentResult.Success)
        assertEquals(PaymentProvider.GooglePay, paymentResult.provider)
        assertEquals("test_token_123", paymentResult.token)
    }

    @Test
    fun `toPaymentResult converts Cancelled correctly`() {
        val result = GooglePayWebResult.Cancelled

        val paymentResult = result.toPaymentResult()

        assertTrue(paymentResult is PaymentResult.Cancelled)
        assertEquals(PaymentProvider.GooglePay, paymentResult.provider)
    }

    @Test
    fun `toPaymentResult converts Error with LOAD_PAYMENT_DATA_FAILED`() {
        val result = GooglePayWebResult.Error(
            errorCode = GooglePayWebErrorCode.LOAD_PAYMENT_DATA_FAILED,
            additionalMessage = "Network error"
        )

        val paymentResult = result.toPaymentResult()

        assertTrue(paymentResult is PaymentResult.Error)
        assertEquals(PaymentProvider.GooglePay, paymentResult.provider)
        assertEquals(PaymentErrorReason.InternalError, paymentResult.reason)
        assertEquals("Network error", paymentResult.message)
    }

    @Test
    fun `toPaymentResult converts Error with TOKEN_EXTRACTION_FAILED`() {
        val result = GooglePayWebResult.Error(
            errorCode = GooglePayWebErrorCode.TOKEN_EXTRACTION_FAILED
        )

        val paymentResult = result.toPaymentResult()

        assertTrue(paymentResult is PaymentResult.Error)
        assertEquals(PaymentProvider.GooglePay, paymentResult.provider)
        assertEquals(PaymentErrorReason.InternalError, paymentResult.reason)
        assertEquals(GooglePayWebErrorCode.TOKEN_EXTRACTION_FAILED.message, paymentResult.message)
    }

    @Test
    fun `toPaymentResult converts Error with UNKNOWN`() {
        val result = GooglePayWebResult.Error(
            errorCode = GooglePayWebErrorCode.UNKNOWN,
            additionalMessage = "Something went wrong"
        )

        val paymentResult = result.toPaymentResult()

        assertTrue(paymentResult is PaymentResult.Error)
        assertEquals(PaymentProvider.GooglePay, paymentResult.provider)
        assertEquals(PaymentErrorReason.Unknown, paymentResult.reason)
        assertEquals("Something went wrong", paymentResult.message)
    }

    @Test
    fun `Error message returns additionalMessage when provided`() {
        val error = GooglePayWebResult.Error(
            errorCode = GooglePayWebErrorCode.LOAD_PAYMENT_DATA_FAILED,
            additionalMessage = "Custom error message"
        )

        assertEquals("Custom error message", error.message)
    }

    @Test
    fun `Error message returns errorCode message when additionalMessage is null`() {
        val error = GooglePayWebResult.Error(
            errorCode = GooglePayWebErrorCode.TOKEN_EXTRACTION_FAILED
        )

        assertEquals(GooglePayWebErrorCode.TOKEN_EXTRACTION_FAILED.message, error.message)
    }

}
