package com.kttipay.kpayment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kttipay.kpayment.config.PaymentConfig
import com.kttipay.kpayment.screens.ConfigScreen
import com.kttipay.kpayment.screens.PaymentScreen
import com.kttipay.payment.api.PaymentEnvironment
import com.kttipay.payment.api.config.MobilePaymentConfig
import com.kttipay.payment.api.logging.KPaymentLogger
import com.kttipay.payment.api.validation.combineErrors
import com.kttipay.payment.api.validation.getOrNull
import com.kttipay.payment.ui.PaymentManagerProvider
import com.kttipay.payment.ui.currentNativePaymentProvider
import com.kttipay.payment.ui.rememberMobilePaymentManager
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.kimplify.cedar.logging.Cedar
import org.kimplify.cedar.logging.trees.PlatformLogTree

/**
 * Holds the result of payment configuration initialization.
 *
 * @property config The payment configuration, or null if no providers are valid
 * @property error Error message describing configuration issues, or null if valid
 */
@Immutable
data class AppConfigState(
    val config: MobilePaymentConfig?,
    val error: String?
)

/**
 * Creates the app configuration state by validating payment providers.
 * Extracted from composable to keep remember block clean and testable.
 */
private fun createAppConfigState(): AppConfigState {
    val googlePayResult = PaymentConfig.buildGooglePayConfig()
    val applePayResult = PaymentConfig.buildApplePayConfig()

    val googlePay = googlePayResult.getOrNull()
    val applePay = applePayResult.getOrNull()

    val validationErrors = listOf(googlePayResult, applePayResult)
        .combineErrors()
        .takeIf { it.isNotEmpty() }
        ?.joinToString("\n\n")

    val config = if (googlePay != null || applePay != null) {
        MobilePaymentConfig(
            googlePay = googlePay,
            applePayMobile = applePay,
            environment = PaymentEnvironment.Development
        )
    } else {
        null
    }

    val error = when {
        config == null && validationErrors != null ->
            "$validationErrors\n\nPlease configure at least one payment provider."
        else -> validationErrors
    }

    return AppConfigState(config = config, error = error)
}

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

    val configState = remember { createAppConfigState() }
    var configError by remember { mutableStateOf(configState.error) }
    val paymentManager = configState.config?.let { rememberMobilePaymentManager(it) }

    Cedar.d(message = "KPayment library initialized successfully")

    MaterialTheme {
        ConfigErrorDialog(
            error = configError,
            onDismiss = { configError = null }
        )

        if (paymentManager != null) {
            PaymentManagerProvider(manager = paymentManager) {
                MainContent()
            }
        } else {
            NotConfiguredContent()
        }
    }
}

@Composable
private fun ConfigErrorDialog(
    error: String?,
    onDismiss: () -> Unit
) {
    error?.let {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Configuration Error") },
            text = {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent() {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { AppTopBar() }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotConfiguredContent() {
    Scaffold(
        topBar = { AppTopBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Payment Not Configured",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "Please configure at least one payment provider (Google Pay or Apple Pay) in PaymentConfig.kt",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar() {
    TopAppBar(
        title = { Text("KPayment Sample") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}
