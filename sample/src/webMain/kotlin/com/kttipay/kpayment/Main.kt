package com.kttipay.kpayment

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow

/**
 * Entry point for the web application.
 *
 * This function is called when the app is loaded in a browser.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow {
        WebApp()
    }
}
