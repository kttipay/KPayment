package com.kttipay.payment.internal.applepay

import com.kttipay.payment.api.PaymentErrorReason
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplePayWebResultTest {

    @Test
    fun `toPaymentResult converts Success correctly`() {
        val result = ApplePayWebResult.Success(token = "apple_pay_token_xyz")

        val paymentResult = result.toPaymentResult()

        assertTrue(paymentResult is PaymentResult.Success)
        assertEquals(PaymentProvider.ApplePay, paymentResult.provider)
        assertEquals("apple_pay_token_xyz", paymentResult.token)
    }

    @Test
    fun `toPaymentResult converts Cancelled correctly`() {
        val result = ApplePayWebResult.Cancelled

        val paymentResult = result.toPaymentResult()

        assertTrue(paymentResult is PaymentResult.Cancelled)
        assertEquals(PaymentProvider.ApplePay, paymentResult.provider)
    }

    @Test
    fun `toPaymentResult converts Failure with SESSION_BEGIN_FAILED`() {
        val result = ApplePayWebResult.Failure(
            errorCode = ApplePayWebErrorCode.SESSION_BEGIN_FAILED,
            additionalMessage = "Session could not start"
        )

        val paymentResult = result.toPaymentResult()

        assertTrue(paymentResult is PaymentResult.Error)
        assertEquals(PaymentProvider.ApplePay, paymentResult.provider)
        assertEquals(PaymentErrorReason.NotAvailable, paymentResult.reason)
        assertEquals("Session could not start", paymentResult.message)
    }

    @Test
    fun `toPaymentResult converts Failure with MERCHANT_VALIDATION_FAILED`() {
        val result = ApplePayWebResult.Failure(
            errorCode = ApplePayWebErrorCode.MERCHANT_VALIDATION_FAILED,
            additionalMessage = "Invalid merchant configuration"
        )

        val paymentResult = result.toPaymentResult()

        assertTrue(paymentResult is PaymentResult.Error)
        assertEquals(PaymentProvider.ApplePay, paymentResult.provider)
        assertEquals(PaymentErrorReason.NetworkError, paymentResult.reason)
        assertEquals("Invalid merchant configuration", paymentResult.message)
    }

    @Test
    fun `toPaymentResult converts Failure with TOKEN_EXTRACTION_FAILED`() {
        val result = ApplePayWebResult.Failure(
            errorCode = ApplePayWebErrorCode.TOKEN_EXTRACTION_FAILED
        )

        val paymentResult = result.toPaymentResult()

        assertTrue(paymentResult is PaymentResult.Error)
        assertEquals(PaymentProvider.ApplePay, paymentResult.provider)
        assertEquals(PaymentErrorReason.InternalError, paymentResult.reason)
        assertEquals(ApplePayWebErrorCode.TOKEN_EXTRACTION_FAILED.message, paymentResult.message)
    }

    @Test
    fun `toPaymentResult converts Failure with UNKNOWN`() {
        val result = ApplePayWebResult.Failure(
            errorCode = ApplePayWebErrorCode.UNKNOWN,
            additionalMessage = "Unexpected error occurred"
        )

        val paymentResult = result.toPaymentResult()

        assertTrue(paymentResult is PaymentResult.Error)
        assertEquals(PaymentProvider.ApplePay, paymentResult.provider)
        assertEquals(PaymentErrorReason.Unknown, paymentResult.reason)
        assertEquals("Unexpected error occurred", paymentResult.message)
    }

    @Test
    fun `Failure message returns additionalMessage when provided`() {
        val failure = ApplePayWebResult.Failure(
            errorCode = ApplePayWebErrorCode.SESSION_BEGIN_FAILED,
            additionalMessage = "Custom error"
        )

        assertEquals("Custom error", failure.message)
    }

    @Test
    fun `Failure message returns errorCode message when additionalMessage is null`() {
        val failure = ApplePayWebResult.Failure(
            errorCode = ApplePayWebErrorCode.MERCHANT_VALIDATION_FAILED
        )

        assertEquals(ApplePayWebErrorCode.MERCHANT_VALIDATION_FAILED.message, failure.message)
    }

    @Test
    fun `ApplePayWebErrorCode fromCode returns correct error code`() {
        assertEquals(ApplePayWebErrorCode.SESSION_BEGIN_FAILED, ApplePayWebErrorCode.fromCode("session_begin_failed"))
        assertEquals(ApplePayWebErrorCode.MERCHANT_VALIDATION_FAILED, ApplePayWebErrorCode.fromCode("merchant_validation_failed"))
        assertEquals(ApplePayWebErrorCode.TOKEN_EXTRACTION_FAILED, ApplePayWebErrorCode.fromCode("token_extraction_failed"))
        assertEquals(ApplePayWebErrorCode.UNKNOWN, ApplePayWebErrorCode.fromCode("unknown"))
    }

    @Test
    fun `ApplePayWebErrorCode fromCode returns UNKNOWN for unrecognized code`() {
        assertEquals(ApplePayWebErrorCode.UNKNOWN, ApplePayWebErrorCode.fromCode("invalid_code"))
        assertEquals(ApplePayWebErrorCode.UNKNOWN, ApplePayWebErrorCode.fromCode(""))
    }
}
