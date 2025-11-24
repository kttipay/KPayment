package com.kttipay.payment

import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.config.ApplePayWebConfig
import com.kttipay.payment.api.config.GooglePayWebConfig
import com.kttipay.payment.api.config.WebPaymentConfig
import com.kttipay.payment.capability.PaymentCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for managing web payment capabilities and configuration.
 *
 * This manager handles initialization, capability checking, and configuration
 * for Google Pay and Apple Pay on web platforms (browser).
 *
 * Features:
 * - Direct access to platform configurations
 * - Browser-based capability checking
 *
 * Use dependency injection to obtain an instance of this interface.
 *
 * Example usage:
 * ```kotlin
 * // Create manually or inject via DI
 * val paymentManager = createWebPaymentManager()
 *
 * // Initialize (synchronous, checks capabilities in background)
 * paymentManager.initialize(
 *     config = WebPaymentConfig(
 *         environment = PaymentEnvironment.Production,
 *         googlePay = GooglePayConfig(...),
 *         applePayWeb = ApplePayWebConfig(...)
 *     ),
 *     scope = viewModelScope
 * )
 *
 * // Observe capabilities reactively
 * paymentManager.capabilities.collect { caps ->
 *     if (caps.googlePay.isReady) {
 *         // Google Pay is available
 *     }
 * }
 * ```
 *
 * @see WebPaymentManagerImpl
 */
interface WebPaymentManager {

    /**
     * Flow of payment capabilities that emits updates when capabilities change.
     *
     * This provides a reactive way to observe payment capability changes.
     * Initially emits [PaymentCapabilities.Uninitialized] until [initialize] is called.
     */
    val capabilities: StateFlow<PaymentCapabilities>

    /**
     * Initializes the payment manager with web-specific configuration.
     *
     * This method is synchronous and returns immediately. It sets up the configuration
     * and sets capabilities to [com.kttipay.payment.capability.CapabilityStatus.Checking].
     *
     * After calling this method, you must call [checkCapabilities] to perform the actual
     * capability checks and update the [capabilities] StateFlow.
     *
     * @param config Web payment configuration
     */
    fun initialize(config: WebPaymentConfig)

    /**
     * Performs async capability checks for all configured payment providers.
     *
     * This suspend function checks Apple Pay and Google Pay availability in the browser
     * and updates the [capabilities] StateFlow with the results. It can be called multiple
     * times to re-check capabilities.
     *
     * Errors during capability checking are handled gracefully by setting the provider's
     * capability status to [com.kttipay.payment.capability.CapabilityStatus.NotAvailable].
     *
     * @throws IllegalStateException if [initialize] has not been called
     */
    suspend fun checkCapabilities()

    /**
     * Checks if a specific payment provider is ready to use.
     *
     * @param provider The payment provider to check (GooglePay or ApplePay)
     * @return true if the provider is available and ready, false otherwise
     */
    fun canUse(provider: PaymentProvider): Boolean

    /**
     * Returns the current payment capabilities snapshot.
     *
     * This provides the current state of all configured payment providers.
     *
     * @return Current payment capabilities
     */
    fun currentCapabilities(): PaymentCapabilities

    /**
     * Checks if the payment manager has been initialized.
     *
     * @return true if initialize() has been successfully called, false otherwise
     */
    fun isInitialized(): Boolean

    /**
     * Returns the active Apple Pay web configuration.
     *
     * @return Apple Pay web configuration
     * @throws IllegalStateException if Apple Pay is not configured
     */
    fun applePayConfig(): ApplePayWebConfig

    /**
     * Returns the active Google Pay web configuration.
     *
     * @return Google Pay web configuration
     * @throws IllegalStateException if Google Pay is not configured
     */
    fun googlePayConfig(): GooglePayWebConfig
}
