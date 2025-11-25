package com.kttipay.kpayment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kttipay.kpayment.config.PaymentConfig
import com.kttipay.payment.api.PaymentEnvironment
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.config.ApplePayBaseConfig
import com.kttipay.payment.api.config.ApplePayWebConfig
import com.kttipay.payment.api.config.WebPaymentConfig
import com.kttipay.payment.api.logging.KPaymentLogger
import com.kttipay.payment.capability.CapabilityStatus
import com.kttipay.payment.createWebPaymentManager
import kotlinx.coroutines.launch
import org.kimplify.cedar.logging.Cedar
import org.kimplify.cedar.logging.trees.PlatformLogTree

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

    val paymentManager = remember { createWebPaymentManager() }
    val scope = rememberCoroutineScope()

    var isInitialized by remember { mutableStateOf(false) }
    val capabilities by paymentManager.capabilities.collectAsState()

    LaunchedEffect(Unit) {
        try {
            Cedar.d("Configuring web payment manager...")

            val config = WebPaymentConfig(
                environment = PaymentEnvironment.Development,
                googlePay = PaymentConfig.createGooglePayConfig(),
                applePayWeb = ApplePayWebConfig(
                    domain = "yourdomain.com",
                    merchantValidationEndpoint = "/api/apple-pay/validate-merchant",
                    baseUrl = "https://yourdomain.com",
                    base = ApplePayBaseConfig(
                        merchantName = "KTTIPAY PTY LTD",
                        currencyCode = PaymentConfig.CURRENCY_CODE,
                        countryCode = PaymentConfig.COUNTRY_CODE,
                        supportedNetworks = PaymentConfig.APPLE_PAY_NETWORKS,
                        merchantCapabilities = PaymentConfig.APPLE_PAY_MERCHANT_CAPABILITIES
                    )
                )
            )

            paymentManager.initialize(config)
            isInitialized = true
            Cedar.i("Web payment manager initialized")

            Cedar.d("Checking payment capabilities...")
            paymentManager.checkCapabilities()
            Cedar.i("Capability check complete")
        } catch (e: Exception) {
            Cedar.e("Error initializing payment manager", e)
        }
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("KPayment Web Sample") },
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Payment Manager Status",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = if (isInitialized) "✓ Initialized" else "⏳ Initializing...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isInitialized)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Google Pay Status
                PaymentProviderCard(
                    providerName = "Google Pay",
                    status = capabilities.googlePay,
                    icon = "💳",
                    onTest = {
                        scope.launch {
                            Cedar.i("Testing Google Pay payment...")
                            // Launch Google Pay payment flow here
                        }
                    }
                )

                // Apple Pay Status
                PaymentProviderCard(
                    providerName = "Apple Pay",
                    status = capabilities.applePay,
                    icon = "🍎",
                    onTest = {
                        scope.launch {
                            Cedar.i("Testing Apple Pay payment...")
                            // Launch Apple Pay payment flow here
                        }
                    }
                )

                // Configuration Info
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

@Composable
private fun PaymentProviderCard(
    providerName: String,
    status: CapabilityStatus,
    icon: String,
    onTest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                            is CapabilityStatus.Ready -> "✓ Available"
                            is CapabilityStatus.NotAvailable -> "✗ Not Available"
                            is CapabilityStatus.Checking -> "⏳ Checking..."
                            is CapabilityStatus.NotConfigured -> "⚠ Not Configured"
                            is CapabilityStatus.Error -> "⚠ Error: ${status.message}"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = when (status) {
                            is CapabilityStatus.Ready -> MaterialTheme.colorScheme.primary
                            is CapabilityStatus.NotAvailable -> MaterialTheme.colorScheme.error
                            is CapabilityStatus.Checking -> MaterialTheme.colorScheme.onSurfaceVariant
                            is CapabilityStatus.NotConfigured -> MaterialTheme.colorScheme.tertiary
                            is CapabilityStatus.Error -> MaterialTheme.colorScheme.error
                        }
                    )
                }
            }

            if (status is CapabilityStatus.Ready) {
                Button(
                    onClick = onTest,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Test")
                }
            }
        }
    }
}
