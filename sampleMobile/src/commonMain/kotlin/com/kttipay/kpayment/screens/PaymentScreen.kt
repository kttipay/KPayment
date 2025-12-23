package com.kttipay.kpayment.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kttipay.kpayment.config.PaymentConfig
import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.PaymentResult
import com.kttipay.payment.ui.LocalMobilePaymentManager
import com.kttipay.payment.ui.NativePaymentTheme
import com.kttipay.payment.ui.NativePaymentType
import com.kttipay.payment.ui.PaymentButton
import com.kttipay.payment.ui.rememberNativePaymentLauncher
import org.kimplify.cedar.logging.Cedar

/**
 * Unified payment demonstration screen for both Android and iOS.
 *
 * Shows:
 * - Payment capability status with clear visual indicators
 * - Platform-specific payment button (Google Pay on Android, Apple Pay on iOS)
 * - Payment amount configuration
 * - Payment result handling with detailed logging
 */
@Composable
fun PaymentScreen(provider: PaymentProvider) {
    var paymentResult by remember { mutableStateOf<PaymentResult?>(null) }
    var amount by remember { mutableStateOf(PaymentConfig.PAYMENT_AMOUNT) }
    var isLoading by remember { mutableStateOf(false) }
    val logger = remember { Cedar.tag(provider::class.simpleName.toString()) }

    val paymentManager = LocalMobilePaymentManager.current

    val isAvailable by paymentManager.observeAvailability(provider)
        .collectAsStateWithLifecycle(false)

    LaunchedEffect(isAvailable) {
        logger.i(
            message = "Availability changed: $isAvailable"
        )
    }

    val launcher = rememberNativePaymentLauncher(
        onResult = { result ->
            when (result) {
                is PaymentResult.Cancelled -> {
                    logger.w(
                        message = "Payment was cancelled by user"
                    )
                    isLoading = false
                    paymentResult = result
                }

                is PaymentResult.Error -> {
                    logger.i(message = "Payment failed: ${result.message}",)
                    isLoading = false
                    paymentResult = result
                }

                is PaymentResult.Success -> {
                    logger.i(
                        message = "Payment successful. Token length: ${result.token.length}"
                    )
                    isLoading = false
                    paymentResult = result
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "${provider::class.simpleName} Demo",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        StatusCard(
            provider = provider,
            isAvailable = isAvailable
        )

        AmountConfigCard(
            amount = amount,
            onAmountChange = { amount = it }
        )

        if (isAvailable) {
            PaymentButton(
                theme = NativePaymentTheme.Light,
                type = NativePaymentType.TopUp,
                enabled = true,
                radius = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
                    .padding(horizontal = 8.dp),
                onClick = {
                    logger.i(
                        message = "Initiating payment for amount: $amount ${PaymentConfig.CURRENCY_CODE}"
                    )
                    isLoading = true
                    paymentResult = null
                    launcher.launch(amount.toString())
                }
            )
        }


        AnimatedVisibility (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Processing payment...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        paymentResult?.let { result ->
            ResultCard(result = result, provider = provider)
        }

        InstructionsCard(provider = provider)
    }
}

/**
 * Status card showing payment provider availability.
 */
@Composable
private fun StatusCard(
    provider: PaymentProvider,
    isAvailable: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isAvailable) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (isAvailable) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = null,
                tint = if (isAvailable) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onErrorContainer
                },
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(
                    text = "Provider Status",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isAvailable) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    }
                )
                Text(
                    text = if (isAvailable) {
                        "${provider} is ready"
                    } else {
                        "${provider} is not available"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isAvailable) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    }
                )
            }
        }
    }
}

/**
 * Amount configuration card.
 */
@Composable
private fun AmountConfigCard(
    amount: Double,
    onAmountChange: (Double) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Payment Amount",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = PaymentConfig.CURRENCY_CODE,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedTextField(
                    value = amount.toString(),
                    onValueChange = {
                        onAmountChange(it.toDoubleOrNull() ?: PaymentConfig.PAYMENT_AMOUNT)
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}

/**
 * Result card showing payment outcome.
 */
@Composable
private fun ResultCard(
    result: PaymentResult,
    provider: PaymentProvider
) {
    val (icon, iconColor, bgColor, title, message) = when (result) {
        is PaymentResult.Success -> {
            ResultCardData(
                icon = Icons.Default.CheckCircle,
                iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                bgColor = MaterialTheme.colorScheme.primaryContainer,
                title = "Payment Successful",
                message = "Token received (${result.token.length} chars):\n${result.token.take(100)}..."
            )
        }

        is PaymentResult.Error -> {
            ResultCardData(
                icon = Icons.Default.Error,
                iconColor = MaterialTheme.colorScheme.onErrorContainer,
                bgColor = MaterialTheme.colorScheme.errorContainer,
                title = "Payment Failed",
                message = "Error: ${result.message}\nReason: ${result.reason}"
            )
        }

        is PaymentResult.Cancelled -> {
            ResultCardData(
                icon = Icons.Default.Info,
                iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                bgColor = MaterialTheme.colorScheme.surfaceVariant,
                title = "Payment Cancelled",
                message = "User cancelled the payment"
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = bgColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = iconColor
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = iconColor
            )
        }
    }
}

private data class ResultCardData(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconColor: Color,
    val bgColor: Color,
    val title: String,
    val message: String
)

/**
 * Instructions card.
 */
@Composable
private fun InstructionsCard(provider: PaymentProvider) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Setup Instructions",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = buildInstructions(provider),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

private fun buildInstructions(provider: PaymentProvider): String {
    return when (provider) {
        PaymentProvider.GooglePay -> """
            1. Configure Google Pay credentials in PaymentConfig.kt
            2. Ensure valid payment gateway setup
            3. Test with TEST environment first
            4. Token can be sent to your backend for processing
            5. Check logs for detailed information
        """.trimIndent()

        PaymentProvider.ApplePay -> """
            1. Configure Apple Pay merchant ID in PaymentConfig.kt
            2. Add Apple Pay capability in Xcode
            3. Configure merchant identity certificate
            4. Test on real device with cards in Wallet
            5. Token can be sent to your backend for processing
            6. Check logs for detailed information
        """.trimIndent()
    }
}
