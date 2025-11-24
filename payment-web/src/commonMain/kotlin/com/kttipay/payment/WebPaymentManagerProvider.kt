package com.kttipay.payment

import com.kttipay.payment.api.config.WebPaymentConfig

/**
 * Global provider for [WebPaymentManager] on web platforms.
 *
 * This singleton provides convenient access to the payment manager instance
 * without requiring dependency injection, making it easy to use in ViewModels
 * and other non-Composable contexts.
 *
 * **Initialization:**
 * ```kotlin
 * // In DI module (Koin)
 * val webModule = module {
 *     single<WebPaymentManager> {
 *         createWebPaymentManager().apply {
 *             initialize(
 *                 config = WebPaymentConfig(
 *                     environment = PaymentEnvironment.Production,
 *                     googlePay = GooglePayConfig(...),
 *                     applePayWeb = ApplePayWebConfig(...)
 *                 )
 *             )
 *         }
 *     }
 * }
 * ```
 *
 * **Usage in ViewModels:**
 * ```kotlin
 * class PaymentViewModel(private val paymentManager: WebPaymentManager) : ViewModel() {
 *     init {
 *         viewModelScope.launch {
 *             paymentManager.checkCapabilities()
 *         }
 *     }
 *
 *     val isApplePayAvailable = paymentManager.capabilities
 *         .map { it.applePay.isReady }
 *         .stateIn(viewModelScope, SharingStarted.Eagerly, false)
 * }
 * ```
 *
 * **Usage in Composables:**
 * ```kotlin
 * @Composable
 * fun PaymentScreen() {
 *     // Option 1: Use global provider
 *     val manager = WebPaymentManagerProvider.instance
 *
 *     // Option 2: Use CompositionLocal (if provided)
 *     val manager = LocalWebPaymentManager.current
 * }
 * ```
 *
 * **Testing:**
 * ```kotlin
 * @Test
 * fun testPayment() = runTest {
 *     WebPaymentManagerProvider.initialize(testConfig)
 *     WebPaymentManagerProvider.checkCapabilities()
 *     // ... test code ...
 *     WebPaymentManagerProvider.reset()  // Clean up
 * }
 * ```
 */
object WebPaymentManagerProvider {

    private var _instance: WebPaymentManager? = null

    /**
     * Returns the initialized [WebPaymentManager] instance.
     *
     * @throws IllegalStateException if [initialize] has not been called yet
     */
    val instance: WebPaymentManager
        get() = _instance ?: error(
            "WebPaymentManager has not been initialized. " +
            "Call WebPaymentManagerProvider.initialize(config) before accessing the instance."
        )

    /**
     * Checks if the payment manager has been initialized.
     *
     * @return true if initialized, false otherwise
     */
    fun isInitialized(): Boolean = _instance != null

    /**
     * Initializes the global [WebPaymentManager] instance with the given configuration.
     *
     * This method is synchronous and returns immediately. It sets up the configuration
     * and sets capabilities to Checking state. You must call [checkCapabilities] separately
     * to perform the actual capability checks.
     *
     * This should be called once at application startup, typically in a DI module.
     *
     * @param config Web payment configuration
     * @throws IllegalStateException if already initialized
     */
    fun initialize(config: WebPaymentConfig): WebPaymentManager {
        if (_instance != null) {
            error(
                "WebPaymentManager is already initialized. " +
                "Call reset() first if you need to reinitialize."
            )
        }

        _instance = createWebPaymentManager().apply {
            initialize(config)
        }
        return instance
    }

    /**
     * Performs async capability checks for all configured payment providers.
     *
     * This suspend function checks Apple Pay and Google Pay availability and updates
     * the capabilities StateFlow. It can be called multiple times to re-check.
     *
     * @throws IllegalStateException if [initialize] has not been called
     */
    suspend fun checkCapabilities() {
        instance.checkCapabilities()
    }

    /**
     * Resets the payment manager instance.
     *
     * This is primarily useful for testing. In production, you typically
     * initialize once and never reset.
     *
     * **Warning:** Calling this will invalidate all existing references
     * to the payment manager instance.
     */
    fun reset() {
        _instance = null
    }
}
