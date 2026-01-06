package com.kttipay.payment.api

/**
 * Payment environment configuration.
 *
 * Use this enum to specify whether you're running in a development/testing environment
 * or production environment. This affects how payment providers are initialized and
 * which endpoints are used.
 *
 * Example usage:
 * ```
 * val config = MobilePaymentConfig(
 *     environment = PaymentEnvironment.Development,
 *     googlePay = googlePayConfig
 * )
 *
 * val productionConfig = MobilePaymentConfig(
 *     environment = PaymentEnvironment.Production,
 *     googlePay = googlePayConfig
 * )
 * ```
 */
enum class PaymentEnvironment {
    /**
     * Production environment for live payments.
     * Use this when your app is released and processing real payments.
     */
    Production,

    /**
     * Development environment for testing.
     * Use this during development and testing with test cards and sandbox accounts.
     */
    Development;

    /**
     * Returns true if this is a debug/development environment, false for production.
     */
    val isDebug: Boolean
        get() = this != Production
}
