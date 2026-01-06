package com.kttipay.payment.internal.utils

import kotlinx.browser.document
import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.dom.HTMLScriptElement
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.unsafeCast

object ScriptLoader {
    private val loadedScripts = mutableSetOf<String>()

    @OptIn(ExperimentalWasmJsInterop::class)
    private suspend fun loadScript(src: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        if (loadedScripts.contains(src)) {
            continuation.resume(Result.success(Unit))
            return@suspendCancellableCoroutine
        }

        val script = document.createElement("script").unsafeCast<HTMLScriptElement>()

        script.onload = {
            loadedScripts.add(src)
            continuation.resume(Result.success(Unit))
        }

        script.onerror = { _, _, _, _, _ ->
            continuation.resumeWithException(Exception("Failed to load script: $src"))
            null
        }

        script.src = src
        script.async = true
        document.head?.appendChild(script)

        continuation.invokeOnCancellation {
            script.remove()
        }
    }

    internal suspend fun loadGooglePayScript(): Result<Unit> {
        return loadScript("https://pay.google.com/gp/p/js/pay.js")
    }
}
