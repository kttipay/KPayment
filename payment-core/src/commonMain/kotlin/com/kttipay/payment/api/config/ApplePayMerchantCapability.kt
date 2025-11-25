package com.kttipay.payment.api.config

enum class ApplePayMerchantCapability {
    CAPABILITY_3DS,
    CAPABILITY_DEBIT,
    CAPABILITY_CREDIT,
    CAPABILITY_EMV;

    val value: String
        get() = when (this) {
            CAPABILITY_3DS -> "supports3DS"
            CAPABILITY_DEBIT -> "supportsDebit"
            CAPABILITY_CREDIT -> "supportsCredit"
            CAPABILITY_EMV -> "supportsEMV"
        }

    companion object {
        val DEFAULT = setOf(CAPABILITY_3DS, CAPABILITY_DEBIT)

        fun fromValue(value: String): ApplePayMerchantCapability? = when (value.lowercase()) {
            "supports3ds" -> CAPABILITY_3DS
            "supportsdebit" -> CAPABILITY_DEBIT
            "supportscredit" -> CAPABILITY_CREDIT
            "supportsemv" -> CAPABILITY_EMV
            else -> null
        }
    }
}
