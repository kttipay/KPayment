package com.kttipay.payment.api

sealed interface PaymentProvider {
    data object GooglePay : PaymentProvider
    data object ApplePay : PaymentProvider
}

