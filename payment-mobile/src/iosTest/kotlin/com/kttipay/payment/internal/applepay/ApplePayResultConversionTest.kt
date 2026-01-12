package com.kttipay.payment.internal.applepay

import com.kttipay.payment.api.PaymentErrorReason
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplePayResultConversionTest {

    @Test
    fun `ApplePayResult Success converts to PaymentResult Success`() {
        val applePayResult = ApplePayResult.Success(
            tokenJson = "{\"token\":\"test_token\"}",
            transactionIdentifier = "txn_123"
        )

        val paymentResult = applePayResult.toPaymentResult()

        assertTrue(paymentResult is PaymentResult.Success)
        assertEquals(PaymentProvider.ApplePay, paymentResult.provider)
        assertEquals("{\"token\":\"test_token\"}", paymentResult.token)
    }

    @Test
    fun `ApplePayResult Success with null transactionIdentifier converts correctly`() {
        val applePayResult = ApplePayResult.Success(
            tokenJson = "{\"token\":\"test_token\"}",
            transactionIdentifier = null
        )

        val paymentResult = applePayResult.toPaymentResult()

        assertTrue(paymentResult is PaymentResult.Success)
        assertEquals(PaymentProvider.ApplePay, paymentResult.provider)
    }

    @Test
    fun `ApplePayResult Cancelled converts to PaymentResult Cancelled`() {
        val applePayResult = ApplePayResult.Cancelled

        val paymentResult = applePayResult.toPaymentResult()

        assertTrue(paymentResult is PaymentResult.Cancelled)
        assertEquals(PaymentProvider.ApplePay, paymentResult.provider)
    }

    @Test
    fun `ApplePayResult Failure with PRESENT_FAILED converts to NotAvailable`() {
        val applePayResult = ApplePayResult.Failure(
            errorCode = ApplePayErrorCode.PRESENT_FAILED,
            additionalMessage = "Could not show payment sheet"
        )

        val paymentResult = applePayResult.toPaymentResult()

        assertTrue(paymentResult is PaymentResult.Error)
        assertEquals(PaymentProvider.ApplePay, paymentResult.provider)
        assertEquals(PaymentErrorReason.NotAvailable, paymentResult.reason)
        assertEquals("Could not show payment sheet", paymentResult.message)
    }

    @Test
    fun `ApplePayResult Failure with TOKEN_EXTRACTION_FAILED converts to InternalError`() {
        val applePayResult = ApplePayResult.Failure(
            errorCode = ApplePayErrorCode.TOKEN_EXTRACTION_FAILED
        )

        val paymentResult = applePayResult.toPaymentResult()

        assertTrue(paymentResult is PaymentResult.Error)
        assertEquals(PaymentProvider.ApplePay, paymentResult.provider)
        assertEquals(PaymentErrorReason.InternalError, paymentResult.reason)
        assertEquals(ApplePayErrorCode.TOKEN_EXTRACTION_FAILED.message, paymentResult.message)
    }

    @Test
    fun `ApplePayResult Failure with AUTHORIZATION_FAILED converts to DeveloperError`() {
        val applePayResult = ApplePayResult.Failure(
            errorCode = ApplePayErrorCode.AUTHORIZATION_FAILED,
            additionalMessage = "Invalid merchant configuration"
        )

        val paymentResult = applePayResult.toPaymentResult()

        assertTrue(paymentResult is PaymentResult.Error)
        assertEquals(PaymentProvider.ApplePay, paymentResult.provider)
        assertEquals(PaymentErrorReason.DeveloperError, paymentResult.reason)
        assertEquals("Invalid merchant configuration", paymentResult.message)
    }

    @Test
    fun `ApplePayResult Failure with UNKNOWN converts to Unknown`() {
        val applePayResult = ApplePayResult.Failure(
            errorCode = ApplePayErrorCode.UNKNOWN,
            additionalMessage = "Unexpected error"
        )

        val paymentResult = applePayResult.toPaymentResult()

        assertTrue(paymentResult is PaymentResult.Error)
        assertEquals(PaymentProvider.ApplePay, paymentResult.provider)
        assertEquals(PaymentErrorReason.Unknown, paymentResult.reason)
        assertEquals("Unexpected error", paymentResult.message)
    }

    @Test
    fun `Failure message returns additionalMessage when provided`() {
        val failure = ApplePayResult.Failure(
            errorCode = ApplePayErrorCode.PRESENT_FAILED,
            additionalMessage = "Custom error message"
        )

        assertEquals("Custom error message", failure.message)
    }

    @Test
    fun `Failure message returns errorCode message when additionalMessage is null`() {
        val failure = ApplePayResult.Failure(
            errorCode = ApplePayErrorCode.TOKEN_EXTRACTION_FAILED
        )

        assertEquals(ApplePayErrorCode.TOKEN_EXTRACTION_FAILED.message, failure.message)
    }

    @Test
    fun `ApplePayErrorCode toPaymentErrorReason maps PRESENT_FAILED to NotAvailable`() {
        val reason = ApplePayErrorCode.PRESENT_FAILED.toPaymentErrorReason()
        assertEquals(PaymentErrorReason.NotAvailable, reason)
    }

    @Test
    fun `ApplePayErrorCode toPaymentErrorReason maps TOKEN_EXTRACTION_FAILED to InternalError`() {
        val reason = ApplePayErrorCode.TOKEN_EXTRACTION_FAILED.toPaymentErrorReason()
        assertEquals(PaymentErrorReason.InternalError, reason)
    }

    @Test
    fun `ApplePayErrorCode toPaymentErrorReason maps AUTHORIZATION_FAILED to DeveloperError`() {
        val reason = ApplePayErrorCode.AUTHORIZATION_FAILED.toPaymentErrorReason()
        assertEquals(PaymentErrorReason.DeveloperError, reason)
    }

    @Test
    fun `ApplePayErrorCode toPaymentErrorReason maps UNKNOWN to Unknown`() {
        val reason = ApplePayErrorCode.UNKNOWN.toPaymentErrorReason()
        assertEquals(PaymentErrorReason.Unknown, reason)
    }

    @Test
    fun `ApplePayErrorCode fromCode returns correct error code`() {
        assertEquals(ApplePayErrorCode.PRESENT_FAILED, ApplePayErrorCode.fromCode("present_failed"))
        assertEquals(ApplePayErrorCode.TOKEN_EXTRACTION_FAILED, ApplePayErrorCode.fromCode("token_extraction_failed"))
        assertEquals(ApplePayErrorCode.AUTHORIZATION_FAILED, ApplePayErrorCode.fromCode("authorization_failed"))
        assertEquals(ApplePayErrorCode.UNKNOWN, ApplePayErrorCode.fromCode("unknown"))
    }

    @Test
    fun `ApplePayErrorCode fromCode returns UNKNOWN for unrecognized code`() {
        assertEquals(ApplePayErrorCode.UNKNOWN, ApplePayErrorCode.fromCode("invalid_code"))
        assertEquals(ApplePayErrorCode.UNKNOWN, ApplePayErrorCode.fromCode(""))
    }
}
