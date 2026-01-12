package com.kttipay.payment

import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.config.PlatformPaymentConfig
import com.kttipay.payment.capability.PaymentCapabilities
import kotlinx.coroutines.flow.Flow

/**
 * Unified interface for managing payment capabilities and configuration.
 *
 * This manager handles capability checking and configuration
 * for Google Pay and Apple Pay across all platforms (Mobile & Web).
 *
 * Capabilities are checked lazily on first use. The manager delegates to platform SDKs
 * (Google Pay API, Apple Pay API) which handle their own caching and state management.
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
 * // Reactive UI binding (most common)
 * val isReady by manager.observeAvailability(PaymentProvider.GooglePay)
 *     .collectAsState(initial = false)
 *
 * // Explicit check when needed
 * val capabilities = manager.checkCapabilities()
 * if (capabilities.canPayWith(PaymentProvider.GooglePay)) {
 *     launcher.launch("10.00")
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
     * Checks current payment provider capabilities by querying platform SDKs.
     *
     * This method delegates to native payment APIs (Google Pay, Apple Pay) which
     * manage their own caching. On first call, triggers lazy initialization.
     * Subsequent calls return current state from platform SDKs.
     *
     * Use this when you need to explicitly check capabilities at a specific point.
     * For reactive UI updates, prefer [observeCapabilities] or [observeAvailability].
     *
     * @return Current payment capabilities
     */
    suspend fun checkCapabilities(): PaymentCapabilities

    /**
     * Observes payment capabilities reactively.
     *
     * Triggers lazy capability checking on first subscription. The flow emits
     * the latest capabilities state and updates when platform conditions change.
     *
     * Use this for reactive UI that displays detailed capability status or
     * needs to observe multiple providers simultaneously.
     *
     * @return Flow that emits current payment capabilities
     */
    fun observeCapabilities(): Flow<PaymentCapabilities>

    /**
     * Observes availability of a specific payment provider reactively.
     *
     * Convenience method that filters [observeCapabilities] for a single provider.
     * Triggers lazy capability checking on first subscription.
     *
     * Use this for reactive UI that only needs boolean ready/not-ready state
     * for a specific payment provider.
     *
     * @param provider The payment provider to observe
     * @return Flow that emits true when provider is ready, false otherwise
     */
    fun observeAvailability(provider: PaymentProvider): Flow<Boolean>
}
