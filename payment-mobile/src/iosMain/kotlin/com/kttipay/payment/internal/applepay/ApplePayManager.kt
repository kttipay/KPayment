package com.kttipay.payment.internal.applepay

/**
 * Optional manager for customizing Apple Pay behavior.
 *
 * By default, the library uses KotlinNativeApplePayFactory automatically.
 * This manager allows advanced users to provide a custom factory implementation
 * if they need specialized behavior.
 *
 * Usage (optional):
 * ```kotlin
 * // Set a custom factory before creating payment launchers
 * IosApplePayManager.setCustomFactory(MyCustomFactory())
 * ```
 */
object IosApplePayManager {
    private var customFactory: ApplePayFactory? = null

    /**
     * Set a custom Apple Pay factory.
     * This is optional - if not set, the default KotlinNativeApplePayFactory is used.
     *
     * @param factory Custom factory implementation
     */
    fun setCustomFactory(factory: ApplePayFactory) {
        customFactory = factory
    }

    /**
     * Clear the custom factory, reverting to the default implementation.
     */
    fun clearCustomFactory() {
        customFactory = null
    }

    /**
     * Get the factory to use for Apple Pay operations.
     * Returns the custom factory if set, otherwise creates a default one.
     *
     * @return ApplePayFactory instance
     */
    internal fun getFactory(): ApplePayFactory {
        return customFactory ?: KotlinNativeApplePayFactory()
    }

    /**
     * Check if a custom factory has been set.
     *
     * @return true if custom factory is set, false otherwise
     */
    fun hasCustomFactory(): Boolean = customFactory != null
}
