package com.kttipay.payment.internal.applepay

import platform.UIKit.UIViewController

/**
 * Provider interface for obtaining the presenting UIViewController for Apple Pay.
 *
 * The library uses this to get the current view controller context when presenting
 * the Apple Pay authorization sheet (PKPaymentAuthorizationController).
 *
 * By default, the library attempts to find the root view controller automatically.
 * Apps can optionally set a custom provider if they need specific behavior.
 */
interface ApplePayViewControllerProvider {
    /**
     * Returns the UIViewController that should present the Apple Pay sheet.
     * Should return null if no suitable view controller is available.
     */
    fun getCurrentViewController(): UIViewController?
}

/**
 * Default implementation that attempts to find the root view controller
 * from the key window.
 */
internal class DefaultApplePayViewControllerProvider : ApplePayViewControllerProvider {
    override fun getCurrentViewController(): UIViewController? {
        return platform.UIKit.UIApplication.sharedApplication.keyWindow?.rootViewController
    }
}

/**
 * Global access point for the ViewController provider.
 * Apps can optionally set a custom provider, otherwise the default is used.
 */
object ApplePayViewControllerProviderHolder {
    private var customProvider: ApplePayViewControllerProvider? = null
    private val defaultProvider = DefaultApplePayViewControllerProvider()

    /**
     * Set a custom view controller provider.
     * This is optional - the library will use a default provider if not set.
     */
    fun setProvider(provider: ApplePayViewControllerProvider) {
        customProvider = provider
    }

    /**
     * Get the current view controller for presenting Apple Pay.
     */
    internal fun getCurrentViewController(): UIViewController? {
        return (customProvider ?: defaultProvider).getCurrentViewController()
    }
}
