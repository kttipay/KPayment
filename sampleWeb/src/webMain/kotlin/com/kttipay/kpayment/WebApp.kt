package com.kttipay.kpayment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kttipay.payment.api.validation.combineErrors
import com.kttipay.payment.api.validation.getOrNull
import com.kttipay.payment.api.PaymentEnvironment
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult
import com.kttipay.payment.api.config.WebPaymentConfig
import com.kttipay.payment.api.logging.KPaymentLogger
import com.kttipay.payment.capability.CapabilityStatus
import com.kttipay.payment.ui.LocalWebPaymentManager
import com.kttipay.payment.ui.PaymentManagerProvider
import com.kttipay.payment.ui.launcher.rememberGooglePayWebLauncher
import com.kttipay.payment.ui.rememberWebPaymentManager
import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.map
import org.kimplify.cedar.logging.Cedar
import org.kimplify.cedar.logging.trees.PlatformLogTree

/**
 * Holds the result of web payment configuration initialization.
 *
 * @property config The payment configuration, or null if no providers are valid
 * @property error Error message describing configuration issues, or null if valid
 */
@Immutable
data class WebAppConfigState(
    val config: WebPaymentConfig?,
    val error: String?
)

/**
 * Creates the web app configuration state by validating payment providers.
 * Extracted from composable to keep remember block clean and testable.
 */
private fun createWebAppConfigState(): WebAppConfigState {
    val googlePayResult = PaymentConfig.buildGooglePayConfig()
    val applePayResult = PaymentConfig.buildApplePayWebConfig()

    val googlePay = googlePayResult.getOrNull()
    val applePay = applePayResult.getOrNull()

    val validationErrors = listOf(googlePayResult, applePayResult)
        .combineErrors()
        .takeIf { it.isNotEmpty() }
        ?.joinToString("\n\n")

    val config = if (googlePay != null || applePay != null) {
        WebPaymentConfig(
            environment = PaymentEnvironment.Development,
            googlePay = googlePay,
            applePayWeb = applePay
        )
    } else {
        null
    }

    val error = when {
        config == null && validationErrors != null ->
            "$validationErrors\n\nPlease configure at least one payment provider."
        else -> validationErrors
    }

    return WebAppConfigState(config = config, error = error)
}

/**
 * Main web application demonstrating KPayment library for web platforms.
 *
 * This sample shows:
 * - Initializing WebPaymentManager
 * - Checking payment capabilities (Google Pay & Apple Pay)
 * - Displaying available payment methods
 * - Handling payment configuration for web
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebApp() {
    LaunchedEffect(Unit) {
        Cedar.plant(PlatformLogTree())
        Cedar.i("Initializing KPayment Web Sample...")
        KPaymentLogger.enabled = true
    }

    val configState = remember { createWebAppConfigState() }
    var configError by remember { mutableStateOf(configState.error) }
    val paymentManager = configState.config?.let { rememberWebPaymentManager(it) }

    MaterialTheme {
        ConfigErrorDialog(
            error = configError,
            onDismiss = { configError = null }
        )

        if (paymentManager != null) {
            PaymentManagerProvider(manager = paymentManager) {
                WebAppMainContent(hasConfigError = configError?.isBlank() == true)
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
private fun NotConfiguredContent() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { WebAppTopBar() }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Payment Not Configured",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Please configure at least one payment provider (Google Pay or Apple Pay) in PaymentConfig.kt",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WebAppTopBar() {
    TopAppBar(
        title = { Text("KPayment Web Sample") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WebAppMainContent(hasConfigError: Boolean) {
    val paymentManager = LocalWebPaymentManager.current

    val isGooglePayAvailable by paymentManager.capabilitiesFlow.map { it.googlePay }
        .collectAsStateWithLifecycle(CapabilityStatus.NotConfigured)
    val isApplePayAvailable by paymentManager.capabilitiesFlow.map { it.applePay }
        .collectAsStateWithLifecycle(CapabilityStatus.NotConfigured)

    val googleButton = if (hasConfigError) rememberGooglePayWebLauncher(
        onResult = { result ->
            when (result) {
                is PaymentResult.Success -> {
                    Cedar.i("Google Pay payment successful: ${result.token}")
                }

                is PaymentResult.Error -> {
                    Cedar.e("Google Pay payment error: ${result.message}")
                }

                is PaymentResult.Cancelled -> {
                    Cedar.i("Google Pay payment cancelled by user")
                }
            }
        }
    ) else null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { WebAppTopBar() }
    ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 700.dp)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PaymentProviderCard(
                        providerName = "Google Pay",
                        status = isGooglePayAvailable,
                        icon = "💳",
                        provider = PaymentProvider.GooglePay,
                        onTest = {
                            googleButton?.launch("1.00")
                        }
                    )

                    PaymentProviderCard(
                        providerName = "Apple Pay",
                        status = isApplePayAvailable,
                        icon = "🍎",
                        provider = PaymentProvider.ApplePay,
                        onTest = {
                            Cedar.i("Testing Apple Pay payment...")
                            // Launch Apple Pay payment flow here when implemented
                        }
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Configuration",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Environment: Development",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Currency: ${PaymentConfig.CURRENCY_CODE}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Country: ${PaymentConfig.COUNTRY_CODE}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // Instructions
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "ℹ️ Setup Instructions",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = """
                                1. Configure your merchant IDs in PaymentConfig.kt
                                2. Set up Apple Pay domain verification
                                3. Configure merchant validation endpoint
                                4. Test payments in a secure context (HTTPS)
                            """.trimIndent(),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
}

/**
 * Styled Apple Pay button following Apple's brand guidelines
 */
@Composable
private fun ApplePayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = " Pay",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Styled Google Pay button following Google's brand guidelines
 */
@Composable
private fun GooglePayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = GoogleIcon,
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(2.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Pay",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PaymentProviderCard(
    providerName: String,
    status: CapabilityStatus,
    icon: String,
    provider: PaymentProvider,
    onTest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = icon,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Column {
                        Text(
                            text = providerName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = when (status) {
                                is CapabilityStatus.Ready -> "✅ Available"
                                is CapabilityStatus.NotSupported -> "❌ Not Available"
                                is CapabilityStatus.Checking -> "⏳ Checking..."
                                is CapabilityStatus.NotConfigured -> "⚠ Not Configured"
                                is CapabilityStatus.Error -> "⚠ Error: ${status.reason}"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = when (status) {
                                is CapabilityStatus.Ready -> MaterialTheme.colorScheme.primary
                                is CapabilityStatus.NotSupported -> MaterialTheme.colorScheme.error
                                is CapabilityStatus.Checking -> MaterialTheme.colorScheme.onSurfaceVariant
                                is CapabilityStatus.NotConfigured -> MaterialTheme.colorScheme.tertiary
                                is CapabilityStatus.Error -> MaterialTheme.colorScheme.error
                            }
                        )
                    }
                }
            }

            if (status is CapabilityStatus.Ready) {
                when (provider) {
                    PaymentProvider.GooglePay -> {
                        GooglePayButton(
                            onClick = onTest,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    PaymentProvider.ApplePay -> {
                        ApplePayButton(
                            onClick = onTest,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
