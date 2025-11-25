package com.kttipay.kpayment.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kttipay.kpayment.config.PaymentConfig

/**
 * Configuration screen showing all payment configuration values.
 *
 * Displays:
 * - Google Pay configuration
 * - Apple Pay configuration
 * - Common payment settings
 * - Instructions for obtaining credentials
 */
@Composable
fun ConfigScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Payment Configuration",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Configure these values in PaymentConfig.kt to use your own payment credentials.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Divider()

        Text(
            text = "Google Pay Configuration",
            style = MaterialTheme.typography.titleLarge
        )

        ConfigItem(
            label = "Merchant Name",
            value = PaymentConfig.GOOGLE_PAY_MERCHANT_NAME,
            description = "Your business name displayed during payment"
        )

        ConfigItem(
            label = "Gateway Merchant ID",
            value = PaymentConfig.GOOGLE_PAY_GATEWAY_MERCHANT_ID,
            description = "Obtained from your payment gateway provider (Stripe, Braintree, etc.)"
        )

        ConfigItem(
            label = "Payment Gateway",
            value = PaymentConfig.GOOGLE_PAY_GATEWAY,
            description = "Your payment gateway identifier (e.g., 'stripe', 'braintree')"
        )

        ConfigItem(
            label = "Environment",
            value = PaymentConfig.GOOGLE_PAY_ENVIRONMENT,
            description = "Use 'TEST' for development, 'PRODUCTION' for live payments"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "How to get Google Pay credentials:",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. Visit https://pay.google.com/business/console\n" +
                            "2. Create or select your merchant account\n" +
                            "3. Configure your payment gateway integration\n" +
                            "4. Get your gateway merchant ID from your payment processor\n" +
                            "5. Test in TEST mode before going to PRODUCTION",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Divider()

        Text(
            text = "Apple Pay Configuration",
            style = MaterialTheme.typography.titleLarge
        )

        ConfigItem(
            label = "Merchant Identifier",
            value = PaymentConfig.APPLE_PAY_MERCHANT_ID,
            description = "Format: merchant.com.yourcompany.yourapp"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "How to get Apple Pay credentials:",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. Visit https://developer.apple.com/account\n" +
                            "2. Go to Certificates, Identifiers & Profiles\n" +
                            "3. Create a new Merchant ID\n" +
                            "4. Create a Payment Processing Certificate\n" +
                            "5. Add Apple Pay capability in Xcode\n" +
                            "6. Configure merchant identity certificate in your Xcode project",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Divider()

        Text(
            text = "Common Settings",
            style = MaterialTheme.typography.titleLarge
        )

        ConfigItem(
            label = "Default Amount",
            value = "$${PaymentConfig.PAYMENT_AMOUNT}",
            description = "Test payment amount"
        )

        ConfigItem(
            label = "Currency Code",
            value = PaymentConfig.CURRENCY_CODE,
            description = "ISO 4217 currency code (e.g., USD, EUR, GBP)"
        )

        ConfigItem(
            label = "Country Code",
            value = PaymentConfig.COUNTRY_CODE,
            description = "ISO 3166-1 alpha-2 country code (e.g., US, GB, AU)"
        )

        ConfigItem(
            label = "Allowed Networks",
            value = PaymentConfig.GOOGLE_PAY_CARD_NETWORKS.joinToString(", "),
            description = "Supported card networks for payments"
        )
    }
}

@Composable
private fun ConfigItem(
    label: String,
    value: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
