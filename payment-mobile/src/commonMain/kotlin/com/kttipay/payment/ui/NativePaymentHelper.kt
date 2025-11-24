package com.kttipay.payment.ui

interface NativePaymentHelper {
    suspend fun isReadyToPay(): Boolean
}
