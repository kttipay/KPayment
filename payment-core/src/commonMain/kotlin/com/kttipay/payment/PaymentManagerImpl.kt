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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Unified implementation of PaymentManager.
 *
 * This class uses Strategy pattern with DI to accept platform-specific implementations
 * of capability checking, platform setup, and configuration access.
 *
 * Capabilities are checked lazily on first use and delegated to platform SDKs
 * which handle their own caching and state management.
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

    private val _capabilitiesFlow = MutableStateFlow(PaymentCapabilities.initial)

    init {
        log.d("Initializing Payment Manager - Environment: ${config.environment.name}")
        platformSetupStrategy.setupPlatformPayments(config)
    }

    override suspend fun checkCapabilities(): PaymentCapabilities {
        log.d("Checking payment capabilities")
        return runCatching {
            val googleStatus = capabilityCheckStrategy.checkGooglePayAvailability(config)
            val appleStatus = capabilityCheckStrategy.checkApplePayAvailability(config)

            PaymentCapabilities(
                googlePay = googleStatus,
                applePay = appleStatus
            ).also { capabilities ->
                _capabilitiesFlow.value = capabilities
                log.d("Capabilities — GooglePay: $googleStatus, ApplePay: $appleStatus")
            }
        }.getOrElse { error ->
            log.e("Capability check failed", error as? Exception)
            PaymentCapabilities(
                googlePay = resolveErrorStatus(configAccessor.getGooglePayConfig(config), error),
                applePay = resolveErrorStatus(configAccessor.getApplePayConfig(config), error)
            ).also { _capabilitiesFlow.value = it }
        }
    }

    override fun observeCapabilities(): Flow<PaymentCapabilities> {
        scope.launch {
            if (_capabilitiesFlow.value.googlePay is CapabilityStatus.Checking ||
                _capabilitiesFlow.value.applePay is CapabilityStatus.Checking
            ) {
                checkCapabilities()
            }
        }
        return _capabilitiesFlow
    }

    override fun observeAvailability(provider: PaymentProvider): Flow<Boolean> {
        return observeCapabilities()
            .map { it.canPayWith(provider) }
            .distinctUntilChanged()
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
