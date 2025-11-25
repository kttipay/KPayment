package com.kttipay.kpayment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kttipay.kpayment.config.PaymentConfig
import com.kttipay.kpayment.screens.ConfigScreen
import com.kttipay.kpayment.screens.PaymentScreen
import com.kttipay.payment.api.PaymentEnvironment
import com.kttipay.payment.api.config.MobilePaymentConfig
import com.kttipay.payment.api.logging.KPaymentLogger
import com.kttipay.payment.ui.LocalMobilePaymentManager
import com.kttipay.payment.ui.currentNativePaymentProvider
import com.kttipay.payment.ui.rememberMobilePaymentManager
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.kimplify.cedar.logging.Cedar
import org.kimplify.cedar.logging.trees.PlatformLogTree

/**
 * Main application composable for the KPayment sample app.
 *
 * Displays a tabbed interface with:
 * - Payment Demo tab (platform-specific payment screens)
 * - Configuration tab (view payment settings)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    SideEffect {
        Cedar.plant(PlatformLogTree())
        Cedar.i(message = "Initializing KPayment library...")
        KPaymentLogger.enabled = true
    }

    val paymentConfig = remember {
        MobilePaymentConfig(
            googlePay = PaymentConfig.createGooglePayConfig(),
            applePayMobile = PaymentConfig.createApplePayConfig(),
            environment = PaymentEnvironment.Development
        )
    }

    val paymentManager = rememberMobilePaymentManager(paymentConfig)


    Cedar.d(message = "KPayment library initialized successfully")

    CompositionLocalProvider(LocalMobilePaymentManager provides paymentManager) {
        MaterialTheme {
            var selectedTabIndex by remember { mutableStateOf(0) }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("KPayment Sample") },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    TabRow(selectedTabIndex = selectedTabIndex) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = { Text("Payment Demo") }
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = { Text("Configuration") }
                        )
                    }

                    when (selectedTabIndex) {
                        0 -> PaymentScreen(currentNativePaymentProvider())
                        1 -> ConfigScreen()
                    }
                }
            }
        }
    }
}