package com.kttipay.payment

import com.kttipay.payment.api.PaymentProvider
import com.kttipay.payment.api.config.PlatformPaymentConfig
import com.kttipay.payment.capability.CapabilityStatus
import com.kttipay.payment.capability.PaymentCapabilities
import com.kttipay.payment.internal.logging.KPaymentLogger
import com.kttipay.payment.strategy.CapabilityCheckStrategy
import com.kttipay.payment.strategy.ConfigAccessor
import com.kttipay.payment.strategy.PlatformSetupStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Unified implementation of PaymentManager.
 *
 * This class uses Strategy pattern with DI to accept platform-specific implementations
 * of capability checking, platform setup, and configuration access.
 *
 * Payment capabilities are checked lazily when [observeAvailability] is first called
 * or when [awaitCapabilities] is invoked.
 *
 * @param config The platform payment configuration
 * @param capabilityCheckStrategy Strategy for checking payment provider availability
 * @param platformSetupStrategy Strategy for platform-specific setup
 * @param configAccessor Strategy for accessing platform-specific configurations
 * @param scope CoroutineScope for async capability checking
 * @param logTag Log tag for this manager instance (defaults to "PaymentManager")
 */
class PaymentManagerImpl<T : PlatformPaymentConfig>(
    override val config: T,
    private val capabilityCheckStrategy: CapabilityCheckStrategy,
    private val platformSetupStrategy: PlatformSetupStrategy,
    private val configAccessor: ConfigAccessor,
    private val scope: CoroutineScope,
    private val logTag: String = "PaymentManager"
) : PaymentManager<T> {

    private val log = KPaymentLogger.tag(logTag)

    private val _capabilitiesFlow = MutableStateFlow(
        PaymentCapabilities(
            googlePay = CapabilityStatus.NotConfigured,
            applePay = CapabilityStatus.NotConfigured
        )
    )

    override val capabilitiesFlow: StateFlow<PaymentCapabilities> = _capabilitiesFlow.asStateFlow()

    private val capabilityCheckMutex = Mutex()
    private var capabilityCheckJob: Job? = null

    init {
        log.d("Initializing Payment Manager - Environment: ${config.environment.name}")
        platformSetupStrategy.setupPlatformPayments(config)
    }

    override suspend fun awaitCapabilities(): PaymentCapabilities {
        ensureCapabilityCheckStarted()
        capabilityCheckJob?.join()
        return _capabilitiesFlow.value
    }

    override suspend fun refreshCapabilities(): PaymentCapabilities {
        log.d("Refreshing payment capabilities")
        return refreshCapabilitiesInternal()
    }

    override fun observeAvailability(provider: PaymentProvider): Flow<Boolean> {
        scope.launch { ensureCapabilityCheckStarted() }
        return capabilitiesFlow
            .map { it.canPayWith(provider) }
            .distinctUntilChanged()
    }

    private suspend fun ensureCapabilityCheckStarted() {
        capabilityCheckMutex.withLock {
            if (capabilityCheckJob == null) {
                capabilityCheckJob = scope.launch {
                    runCatching { refreshCapabilitiesInternal() }
                        .onFailure { error ->
                            log.e("Capability check failed", error as? Exception)
                            updateCapabilitiesOnFailure(error)
                        }
                }
            }
        }
    }

    private suspend fun refreshCapabilitiesInternal(): PaymentCapabilities {
        val googleStatus = capabilityCheckStrategy.checkGooglePayAvailability(config)
        val appleStatus = capabilityCheckStrategy.checkApplePayAvailability(config)

        return PaymentCapabilities(
            googlePay = googleStatus,
            applePay = appleStatus
        ).also { newCapabilities ->
            _capabilitiesFlow.value = newCapabilities
            log.d("Capabilities — GooglePay: $googleStatus, ApplePay: $appleStatus")
        }
    }

    private fun updateCapabilitiesOnFailure(error: Throwable) {
        _capabilitiesFlow.update {
            PaymentCapabilities(
                googlePay = resolveErrorStatus(configAccessor.getGooglePayConfig(config), error),
                applePay = resolveErrorStatus(configAccessor.getApplePayConfig(config), error)
            )
        }
    }

    private fun resolveErrorStatus(configuredProvider: Any?, error: Throwable): CapabilityStatus =
        when {
            configuredProvider != null -> CapabilityStatus.Error(
                reason = "Capability check failed",
                throwable = error as? Exception
            )
            else -> CapabilityStatus.NotConfigured
        }
}
