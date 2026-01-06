package com.kttipay.payment

import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.config.PlatformPaymentConfig
import com.kttipay.payment.capability.PaymentCapabilities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Unified interface for managing payment capabilities and configuration.
 *
 * This manager handles capability checking and configuration
 * for Google Pay and Apple Pay across all platforms (Mobile & Web).
 *
 * Payment capabilities are checked lazily when [observeAvailability] is first called
 * or when [awaitCapabilities] is invoked.
 *
 * Features:
 * - Constructor-based configuration (immutable)
 * - Reactive state updates via Kotlin Flow
 * - Lazy capability checking
 * - Platform-agnostic API
 *
 * Use platform-specific factory functions to create instances:
 * - `createMobilePaymentManager()` for Android/iOS
 * - `createWebPaymentManager()` for Web
 *
 * Example usage:
 * ```kotlin
 * // Reactive UI binding
 * val isReady = manager.observeAvailability(PaymentProvider.GooglePay)
 *     .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)
 *
 * // Suspend when you need actual capabilities
 * val capabilities = manager.awaitCapabilities()
 * if (capabilities.canPayWith(PaymentProvider.GooglePay)) {
 *     // Launch payment
 * }
 * ```
 *
 * @see com.kttipay.payment.PaymentManagerImpl
 */
interface PaymentManager<T : PlatformPaymentConfig> {

    /**
     * The payment configuration used by this manager.
     */
    val config: T

    /**
     * Flow that emits current payment capabilities.
     *
     * Capabilities are checked lazily when [observeAvailability] is called.
     * Subscribe to this flow to observe changes in payment availability.
     *
     * For a quick snapshot, use [capabilitiesFlow.value], but note that
     * capabilities may not be checked yet. Use [awaitCapabilities] to ensure
     * the initial check has completed.
     */
    val capabilitiesFlow: StateFlow<PaymentCapabilities>

    /**
     * Waits for the initial capability check to complete and returns the result.
     *
     * If the check has already completed, returns immediately with cached capabilities.
     * Use this when you need to ensure capabilities are available before proceeding.
     *
     * @return Current payment capabilities after initial check completes
     */
    suspend fun awaitCapabilities(): PaymentCapabilities

    /**
     * Forces a re-check of payment provider availability.
     *
     * Use this when platform conditions may have changed (e.g., user added a card,
     * returned from settings, or network connectivity changed).
     *
     * @return Updated payment capabilities
     */
    suspend fun refreshCapabilities(): PaymentCapabilities

    /**
     * Observes availability of a specific payment provider.
     *
     * This triggers lazy capability checking if not already started.
     * The flow emits updates whenever the provider's availability changes.
     *
     * @param provider The payment provider to observe
     * @return Flow that emits true when provider is ready, false otherwise
     */
    fun observeAvailability(provider: PaymentProvider): Flow<Boolean>
}
