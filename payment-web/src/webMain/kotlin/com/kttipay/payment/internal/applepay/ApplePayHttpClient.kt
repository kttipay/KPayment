package com.kttipay.payment.internal.applepay

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.Promise
import kotlin.js.definedExternally
import kotlin.js.unsafeCast

@OptIn(ExperimentalWasmJsInterop::class)
interface ApplePayHttpClient {
    suspend fun fetchJson(url: String, method: String): JsAny
}

@OptIn(ExperimentalWasmJsInterop::class)
class FetchApplePayHttpClient : ApplePayHttpClient {
    override suspend fun fetchJson(url: String, method: String): JsAny =
        suspendCancellableCoroutine { cont ->
            fetch(url, makeFetchInit(method)).then { respAny ->
                val resp = respAny.unsafeCast<JsResponse>()
                if (!resp.ok) {
                    cont.resumeWithException(RuntimeException("Fetch failed status ${resp.status}"))
                } else {
                    resp.json().then { js ->
                        cont.resume(js)
                        null
                    }.catch { err ->
                        cont.resumeWithException(RuntimeException("JSON parse failed: $err"))
                        null
                    }
                }
                null
            }.catch { err ->
                cont.resumeWithException(RuntimeException("Fetch error: $err"))
                null
            }
        }
}

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("function(url, init) { return fetch(url, init) }")
private external fun fetch(url: String, init: JsAny? = definedExternally): Promise<JsAny>

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("function(method) { return ({ method: method }) }")
private external fun makeFetchInit(method: String): JsAny

@OptIn(ExperimentalWasmJsInterop::class)
private external interface JsResponse : JsAny {
    val ok: Boolean
    val status: Int

    fun json(): Promise<JsAny>
}
