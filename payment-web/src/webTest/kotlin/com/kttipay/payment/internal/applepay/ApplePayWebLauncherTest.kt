package com.kttipay.payment.internal.applepay

import com.kttipay.payment.api.PaymentErrorReason
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult
import com.kttipay.payment.api.config.ApplePayBaseConfig
import com.kttipay.payment.api.config.ApplePayWebConfig
import com.kttipay.payment.internal.applepay.launcher.ApplePayWebLauncher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ApplePayWebLauncherTest {

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


    private fun createLauncher(
        onResult: (PaymentResult) -> Unit = {}
    ): ApplePayWebLauncher {
        val config = ApplePayWebConfig(
            base = ApplePayBaseConfig(merchantName = "Test Merchant"),
            merchantValidationEndpoint = "/validate",
            baseUrl = "https://example.com",
            domain = "example.com"
        )
        return ApplePayWebLauncher(config, onResult)
    }
}
