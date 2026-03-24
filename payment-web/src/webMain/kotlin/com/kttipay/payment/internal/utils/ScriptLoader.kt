package com.kttipay.payment.internal.utils

import com.kttipay.payment.internal.applepay.isApplePaySdkLoaded
import kotlinx.browser.document
import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.dom.HTMLScriptElement
import kotlin.coroutines.resume
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.unsafeCast

object ScriptLoader {
    private val loadedScripts = mutableSetOf<String>()

    private const val APPLE_PAY_SDK_URL =
        "https://applepay.cdn-apple.com/jsapi/1.latest/apple-pay-sdk.js"

    @OptIn(ExperimentalWasmJsInterop::class)
    private suspend fun loadScript(
        src: String,
        async: Boolean = true,
        crossOrigin: String? = null
    ): Result<Unit> = suspendCancellableCoroutine { continuation ->
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
            continuation.resume(Result.failure(Exception("Failed to load script: $src")))
            null
        }

        script.src = src
        script.async = async
        if (crossOrigin != null) {
            script.crossOrigin = crossOrigin
        }
        document.head?.appendChild(script)

        continuation.invokeOnCancellation {
            script.remove()
        }
    }

    internal suspend fun loadGooglePayScript(): Result<Unit> {
        return loadScript("https://pay.google.com/gp/p/js/pay.js")
    }

    /**
     * Loads the Apple Pay JS SDK (v1.2.0+) which enables the QR code payment flow
     * on non-Safari browsers.
     *
     * Skips loading if the SDK is already present (e.g., loaded via a static
     * `<script>` tag in the HTML `<head>`).
     */
    internal suspend fun loadApplePaySdkScript(): Result<Unit> {
        val alreadyLoaded = runCatching { isApplePaySdkLoaded() }.getOrDefault(false)
        if (alreadyLoaded) {
            loadedScripts.add(APPLE_PAY_SDK_URL)
            return Result.success(Unit)
        }
        return runCatching {
            loadScript(
                src = APPLE_PAY_SDK_URL,
                async = false,
                crossOrigin = "anonymous"
            )
        }.getOrElse { Result.failure(it) }
    }
}
