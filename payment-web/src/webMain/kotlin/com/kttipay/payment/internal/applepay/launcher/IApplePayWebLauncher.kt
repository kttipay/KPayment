package com.kttipay.payment.internal.applepay.launcher

import com.kttipay.common.deci.Deci

interface IApplePayWebLauncher {
    fun launch(amount: Deci)
}