package com.kttipay.payment.api.logging

import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("function() { return Date.now(); }")
private external fun jsCurrentTimeMillis(): Double

actual fun currentTimeMillis(): Long = jsCurrentTimeMillis().toLong()