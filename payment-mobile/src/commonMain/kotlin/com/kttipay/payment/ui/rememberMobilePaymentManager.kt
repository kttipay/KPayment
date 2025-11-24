package com.kttipay.payment.ui

import androidx.compose.runtime.Composable
import com.kttipay.payment.MobilePaymentManager
import com.kttipay.payment.api.config.MobilePaymentConfig

/**
 * Creates and remembers a MobilePaymentManager instance with the given configuration.
 *
 * This is a platform-specific composable that automatically creates the appropriate
 * MobilePaymentManager implementation for Android or iOS. The instance is remembered
 * across recompositions based on the config.
 *
 * The manager is configured at construction time and capabilities are checked
 * lazily when the flow is first collected.
 *
 * Example usage:
 * ```kotlin
 * @Composable
 * fun App() {
 *     val config = remember {
 *         MobilePaymentConfig(
 *             googlePay = GooglePayConfig(...),
 *             applePayMobile = ApplePayMobileConfig(...),
 *             environment = PaymentEnvironment.Production
 *         )
 *     }
 *
 *     val paymentManager = rememberMobilePaymentManager(config)
 *     // No initialize() call needed!
 *
 *     // Use payment manager in your UI
 *     PaymentScreen(paymentManager)
 * }
 * ```
 *
 * Note: On Android, this requires a Context which is obtained from LocalContext.current.
 * On iOS, no context is needed.
 *
 * @param config The payment configuration (Google Pay and/or Apple Pay)
 * @return A platform-specific MobilePaymentManager instance
 */
@Composable
expect fun rememberMobilePaymentManager(config: MobilePaymentConfig): MobilePaymentManager