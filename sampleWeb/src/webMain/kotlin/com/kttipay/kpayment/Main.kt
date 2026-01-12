package com.kttipay.kpayment

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.window.ComposeViewport
import com.kttipay.payment.api.logging.KPaymentLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kpayment.sampleweb.generated.resources.Res
import kpayment.sampleweb.generated.resources.noto_color_emoji
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.preloadFont
import org.kimplify.cedar.logging.Cedar
import org.kimplify.cedar.logging.trees.PlatformLogTree

/**
 * Entry point for the web application.
 *
 * This function is called when the app is loaded in a browser.
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
fun main() {
    ComposeViewport {
        val notoColorEmoji by preloadFont(Res.font.noto_color_emoji)
        var fontsLoaded by remember { mutableStateOf(false) }
        val resolver = LocalFontFamilyResolver.current

        LaunchedEffect(Unit) {
            Cedar.plant(PlatformLogTree())
            Cedar.i("Initializing KPayment Web Sample...")
            KPaymentLogger.enabled = true
        }

        LaunchedEffect(notoColorEmoji) {
            val all = listOfNotNull(notoColorEmoji)

            if (all.size == 1) {
                all.map { font ->
                    launch(Dispatchers.Default) { resolver.preload(FontFamily(font)) }
                }.joinAll()

                fontsLoaded = true
            }
        }

        Crossfade(fontsLoaded) {
            if (!it) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                WebApp()
            }
        }
    }
}
