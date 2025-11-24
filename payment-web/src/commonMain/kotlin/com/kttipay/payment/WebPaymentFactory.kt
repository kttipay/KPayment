package com.kttipay.payment

/**
 * Creates a WebPaymentManager instance for web platforms.
 *
 * This is the recommended way to create the web payment manager instance.
 *
 * Example usage:
 * ```kotlin
 * // Create instance
 * val paymentManager = createWebPaymentManager()
 *
 * // Register with DI (Koin example)
 * single<WebPaymentManager> { createWebPaymentManager() }
 * ```
 *
 * @return Configured WebPaymentManager instance
 */
fun createWebPaymentManager(): WebPaymentManager {
    return WebPaymentManagerImpl()
}
