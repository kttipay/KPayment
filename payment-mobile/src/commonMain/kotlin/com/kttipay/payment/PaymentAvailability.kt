package com.kttipay.payment

import com.kttipay.payment.api.PaymentProvider

/**
 * Helper class to check payment provider availability.
 *
 * This class wraps MobilePaymentManager to provide convenient
 * availability checks for payment providers.
 *
 * Inject this class where you need to check payment availability.
 */
class PaymentAvailability(
    private val mobilePaymentManager: MobilePaymentManager
) {
    /**
     * Checks if a specific payment provider is supported and available.
     *
     * @param provider The payment provider to check
     * @return true if the provider is ready to use, false otherwise
     */
    fun isSupported(provider: PaymentProvider): Boolean {
        return mobilePaymentManager.canUse(provider)
    }

    /**
     * Checks if Google Pay is supported and available.
     *
     * @return true if Google Pay is ready to use, false otherwise
     */
    fun isGooglePaySupported(): Boolean = isSupported(PaymentProvider.GooglePay)

    /**
     * Checks if Apple Pay is supported and available.
     *
     * @return true if Apple Pay is ready to use, false otherwise
     */
    fun isApplePaySupported(): Boolean = isSupported(PaymentProvider.ApplePay)
}
