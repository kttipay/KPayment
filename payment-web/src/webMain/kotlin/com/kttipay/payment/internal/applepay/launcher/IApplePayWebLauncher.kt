package com.kttipay.payment.internal.applepay.launcher

import org.kimplify.deci.Deci

interface IApplePayWebLauncher {
    fun launch(amount: Deci)
}