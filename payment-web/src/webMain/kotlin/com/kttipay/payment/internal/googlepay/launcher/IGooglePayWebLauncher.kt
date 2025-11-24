package com.kttipay.payment.internal.googlepay.launcher

import com.kttipay.common.deci.Deci

interface IGooglePayWebLauncher {
    fun launch(amount: Deci)
}