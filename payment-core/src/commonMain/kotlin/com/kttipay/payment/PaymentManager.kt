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
 * The manager is configured at construction time and capabilities are checked
 * lazily when [capabilitiesFlow] is first collected.
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
 * @see com.kttipay.payment.PaymentManagerImpl
 */
interface PaymentManager<T: PlatformPaymentConfig> {
    val config: T

    /**
     * Flow that emits current payment capabilities.
     *
     * Capabilities are checked lazily when this flow is first collected.
     * Subscribe to this to observe changes in payment availability.
     */
    val capabilitiesFlow: StateFlow<PaymentCapabilities>

    /**
     * Re-checks payment provider availability.
     *
     * Use this when platform conditions may have changed (e.g., user added a card).
     *
     * @return Updated payment capabilities
     */
    suspend fun refreshCapabilities(): PaymentCapabilities

    /**
     * Checks if a specific payment provider is ready to use.
     *
     * @param provider The payment provider to check
     * @return true if provider is ready, false otherwise
     */
    fun canUse(provider: PaymentProvider): Boolean

    /**
     * Returns the current payment capabilities snapshot.
     *
     * For reactive updates, use [capabilitiesFlow] or [observeAvailability].
     *
     * @return Current capabilities
     */
    fun currentCapabilities(): PaymentCapabilities

    /**
     * Observes availability of a specific payment provider.
     *
     * @param provider The payment provider to observe
     * @return Flow that emits true when provider is ready, false otherwise
     */
    fun observeAvailability(provider: PaymentProvider): Flow<Boolean>
}
