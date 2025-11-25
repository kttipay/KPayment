package com.kttipay.payment.api.config

/**
 * Supported card networks for Google Pay transactions.
 *
 * These map directly to Google Pay API card network identifiers.
 */
enum class GooglePayCardNetwork {
    AMEX,
    DISCOVER,
    INTERAC,
    JCB,
    MASTERCARD,
    VISA;

    /**
     * The string value used in Google Pay API requests.
     */
    val value: String
        get() = name

    companion object {
        /**
         * Parse a Google Pay API card network string to enum.
         * Returns null if the value is not recognized.
         */
        fun fromValue(value: String): GooglePayCardNetwork? =
            entries.find { it.value.equals(value, ignoreCase = true) }
    }
}
