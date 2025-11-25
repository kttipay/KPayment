package com.kttipay.payment.internal.googlepay.launcher

import org.kimplify.deci.Deci

interface IGooglePayWebLauncher {
    fun launch(amount: Deci)
}