package com.kttipay.payment.internal.googlepay

import com.google.android.gms.common.api.CommonStatusCodes
import com.kttipay.payment.api.PaymentErrorReason
import kotlin.test.Test
import kotlin.test.assertEquals

class GooglePayErrorMappingTest {

    @Test
    fun `statusToPaymentErrorReason maps TIMEOUT to Timeout`() {
        val reason = statusToPaymentErrorReason(CommonStatusCodes.TIMEOUT)
        assertEquals(PaymentErrorReason.Timeout, reason)
    }

    @Test
    fun `statusToPaymentErrorReason maps API_NOT_CONNECTED to ApiNotConnected`() {
        val reason = statusToPaymentErrorReason(CommonStatusCodes.API_NOT_CONNECTED)
        assertEquals(PaymentErrorReason.ApiNotConnected, reason)
    }

    @Test
    fun `statusToPaymentErrorReason maps CONNECTION_SUSPENDED_DURING_CALL to ConnectionSuspendedDuringCall`() {
        val reason = statusToPaymentErrorReason(CommonStatusCodes.CONNECTION_SUSPENDED_DURING_CALL)
        assertEquals(PaymentErrorReason.ConnectionSuspendedDuringCall, reason)
    }

    @Test
    fun `statusToPaymentErrorReason maps DEVELOPER_ERROR to DeveloperError`() {
        val reason = statusToPaymentErrorReason(CommonStatusCodes.DEVELOPER_ERROR)
        assertEquals(PaymentErrorReason.DeveloperError, reason)
    }

    @Test
    fun `statusToPaymentErrorReason maps INTERNAL_ERROR to InternalError`() {
        val reason = statusToPaymentErrorReason(CommonStatusCodes.INTERNAL_ERROR)
        assertEquals(PaymentErrorReason.InternalError, reason)
    }

    @Test
    fun `statusToPaymentErrorReason maps INTERRUPTED to Interrupted`() {
        val reason = statusToPaymentErrorReason(CommonStatusCodes.INTERRUPTED)
        assertEquals(PaymentErrorReason.Interrupted, reason)
    }

    @Test
    fun `statusToPaymentErrorReason maps NETWORK_ERROR to NetworkError`() {
        val reason = statusToPaymentErrorReason(CommonStatusCodes.NETWORK_ERROR)
        assertEquals(PaymentErrorReason.NetworkError, reason)
    }

    @Test
    fun `statusToPaymentErrorReason maps SIGN_IN_REQUIRED to SignInRequired`() {
        val reason = statusToPaymentErrorReason(CommonStatusCodes.SIGN_IN_REQUIRED)
        assertEquals(PaymentErrorReason.SignInRequired, reason)
    }

    @Test
    fun `statusToPaymentErrorReason maps ERROR to Unknown`() {
        val reason = statusToPaymentErrorReason(CommonStatusCodes.ERROR)
        assertEquals(PaymentErrorReason.Unknown, reason)
    }

    @Test
    fun `statusToPaymentErrorReason maps unknown status code to Unknown`() {
        val reason = statusToPaymentErrorReason(999999)
        assertEquals(PaymentErrorReason.Unknown, reason)
    }

    @Test
    fun `statusToPaymentErrorReason maps negative status code to Unknown`() {
        val reason = statusToPaymentErrorReason(-1)
        assertEquals(PaymentErrorReason.Unknown, reason)
    }
}
