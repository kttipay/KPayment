@file:OptIn(ExperimentalWasmJsInterop::class)

package com.kttipay.payment.internal.applepay

import kotlin.JsFun
import kotlin.js.ExperimentalWasmJsInterop

/**
 * Checks whether the Apple Pay JS SDK (v1.2.0+) is already present.
 *
 * Uses two signals:
 * 1. A `<script>` tag with the Apple Pay SDK URL exists in the document
 * 2. The `<apple-pay-button>` custom element is registered
 *
 * Either signal is sufficient to skip dynamic loading.
 */
@JsFun(
    """
    function() {
        // Check if the SDK script tag is already in the DOM
        var scripts = document.querySelectorAll('script[src*="apple-pay-sdk"]');
        if (scripts.length > 0) return true;
        // Check if the custom element is registered
        if (typeof customElements !== 'undefined' && customElements.get('apple-pay-button') !== undefined) return true;
        return false;
    }
    """
)
internal external fun isApplePaySdkLoaded(): Boolean
