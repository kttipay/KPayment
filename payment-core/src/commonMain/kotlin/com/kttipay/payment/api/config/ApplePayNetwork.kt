package com.kttipay.payment.api.config

enum class ApplePayNetwork {
    AMEX,
    DISCOVER,
    MASTERCARD,
    VISA,
    JCB;

    val value: String
        get() = when (this) {
            AMEX -> "amex"
            DISCOVER -> "discover"
            MASTERCARD -> "masterCard"
            VISA -> "visa"
            JCB -> "jcb"
        }

    companion object {
        fun fromValue(value: String): ApplePayNetwork? = when (value.lowercase()) {
            "amex" -> AMEX
            "discover" -> DISCOVER
            "mastercard", "masterCard" -> MASTERCARD
            "visa" -> VISA
            "jcb" -> JCB
            else -> null
        }
    }
}
