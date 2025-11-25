package com.kttipay.payment.api.config

/**
 * Supported authentication methods for Google Pay card transactions.
 *
 * These determine how the card information is authenticated during payment.
 */
enum class GooglePayAuthMethod {
    /**
     * Primary Account Number (PAN) only authentication.
     * The card is authenticated using the PAN and expiry date.
     */
    PAN_ONLY,

    /**
     * 3-D Secure cryptogram authentication.
     * The card uses EMV cryptogram for secure authentication.
     */
    CRYPTOGRAM_3DS;

    /**
     * The string value used in Google Pay API requests.
     */
    val value: String
        get() = name

    companion object {
        /**
         * Default set of authentication methods supporting both PAN and cryptogram.
         */
        val DEFAULT = setOf(PAN_ONLY, CRYPTOGRAM_3DS)

        /**
         * Parse a Google Pay API auth method string to enum.
         * Returns null if the value is not recognized.
         */
        fun fromValue(value: String): GooglePayAuthMethod? =
            entries.find { it.value.equals(value, ignoreCase = true) }
    }
}
