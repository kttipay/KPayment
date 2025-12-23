package com.kttipay.payment.internal.applepay

import com.kttipay.payment.api.config.ApplePayMerchantCapability
import com.kttipay.payment.api.config.ApplePayNetwork
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDecimalNumber
import platform.Foundation.create
import platform.PassKit.PKMerchantCapability3DS
import platform.PassKit.PKMerchantCapabilityCredit
import platform.PassKit.PKMerchantCapabilityDebit
import platform.PassKit.PKMerchantCapabilityEMV
import platform.PassKit.PKPassLibrary
import platform.PassKit.PKPayment
import platform.PassKit.PKPaymentAuthorizationController
import platform.PassKit.PKPaymentAuthorizationControllerDelegateProtocol
import platform.PassKit.PKPaymentAuthorizationResult
import platform.PassKit.PKPaymentAuthorizationStatus
import platform.PassKit.PKPaymentNetworkAmex
import platform.PassKit.PKPaymentNetworkDiscover
import platform.PassKit.PKPaymentNetworkJCB
import platform.PassKit.PKPaymentNetworkMasterCard
import platform.PassKit.PKPaymentNetworkVisa
import platform.PassKit.PKPaymentRequest
import platform.PassKit.PKPaymentSummaryItem
import platform.PassKit.PKPaymentSummaryItemType
import platform.PassKit.PKPaymentToken
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/**
 * Kotlin/Native implementation of ApplePayFactory using PassKit.
 *
 * This implementation uses Kotlin/Native's direct interop with iOS frameworks,
 * requiring no Swift or Objective-C code.
 */
@OptIn(ExperimentalForeignApi::class)
class KotlinNativeApplePayFactory : ApplePayFactory {
    private var currentCompletion: ((ApplePayResult) -> Unit)? = null
    private var currentController: PKPaymentAuthorizationController? = null
    private var currentDelegate: ApplePayDelegate? = null

    override fun applePayStatus(): ApplePayStatus {
        val canPay = PKPaymentAuthorizationController.canMakePayments()
        val defaultNetworks = listOf(
            PKPaymentNetworkAmex,
            PKPaymentNetworkDiscover,
            PKPaymentNetworkMasterCard,
            PKPaymentNetworkVisa
        )
        val canSetup =
            PKPaymentAuthorizationController.canMakePaymentsUsingNetworks(defaultNetworks)

        return ApplePayStatus(
            canMakePayments = canPay,
            canSetupCards = canSetup
        )
    }

    override fun startPayment(
        request: ApplePayRequest,
        onResult: (ApplePayResult) -> Unit
    ) {
        val pkRequest = PKPaymentRequest().apply {
            merchantIdentifier = request.merchantId
            countryCode = request.countryCode
            currencyCode = request.currencyCode

            merchantCapabilities = request.merchantCapabilities.fold(0UL) { acc, capability ->
                acc or when (capability) {
                    ApplePayMerchantCapability.CAPABILITY_3DS -> PKMerchantCapability3DS
                    ApplePayMerchantCapability.CAPABILITY_DEBIT -> PKMerchantCapabilityDebit
                    ApplePayMerchantCapability.CAPABILITY_CREDIT -> PKMerchantCapabilityCredit
                    ApplePayMerchantCapability.CAPABILITY_EMV -> PKMerchantCapabilityEMV
                }
            }

            supportedNetworks = request.supportedNetworks.map { network ->
                when (network) {
                    ApplePayNetwork.AMEX -> PKPaymentNetworkAmex
                    ApplePayNetwork.DISCOVER -> PKPaymentNetworkDiscover
                    ApplePayNetwork.MASTERCARD -> PKPaymentNetworkMasterCard
                    ApplePayNetwork.VISA -> PKPaymentNetworkVisa
                    ApplePayNetwork.JCB -> PKPaymentNetworkJCB
                }
            }

            paymentSummaryItems = request.summaryItems.map { item ->
                PKPaymentSummaryItem.summaryItemWithLabel(
                    label = item.label,
                    amount = NSDecimalNumber(item.amount),
                    type = if (item.isFinal) {
                        PKPaymentSummaryItemType.PKPaymentSummaryItemTypeFinal
                    } else {
                        PKPaymentSummaryItemType.PKPaymentSummaryItemTypePending
                    }
                )
            }
        }

        val delegate = ApplePayDelegate(onResult)
        currentDelegate = delegate

        val controller = PKPaymentAuthorizationController(pkRequest)
        controller.delegate = delegate

        currentController = controller
        currentCompletion = onResult

        controller.presentWithCompletion { presented ->
            if (!presented) {
                onResult(
                    ApplePayResult.Failure(
                        errorCode = ApplePayErrorCode.PRESENT_FAILED
                    )
                )
                cleanup()
            }
        }
    }

    override fun presentSetupFlow(onFinished: (Boolean) -> Unit) {
        PKPassLibrary().openPaymentSetup()
        onFinished(true)
    }

    private fun cleanup() {
        currentCompletion = null
        currentController = null
        currentDelegate = null
    }

    /**
     * Delegate for PKPaymentAuthorizationController
     */
    private inner class ApplePayDelegate(
        private val onResult: (ApplePayResult) -> Unit
    ) : NSObject(), PKPaymentAuthorizationControllerDelegateProtocol {

        private var paymentToken: PKPaymentToken? = null
        private var hasCompletedPayment = false

        override fun paymentAuthorizationController(
            controller: PKPaymentAuthorizationController,
            didAuthorizePayment: PKPayment,
            handler: (PKPaymentAuthorizationResult?) -> Unit
        ) {
            paymentToken = didAuthorizePayment.token
            hasCompletedPayment = true

            val result = PKPaymentAuthorizationResult(
                status = PKPaymentAuthorizationStatus.PKPaymentAuthorizationStatusSuccess,
                errors = null
            )
            handler(result)
        }

        override fun paymentAuthorizationControllerDidFinish(
            controller: PKPaymentAuthorizationController
        ) {
            controller.dismissWithCompletion {

                dispatch_async(dispatch_get_main_queue()) {
                    val token = paymentToken

                    if (token != null && hasCompletedPayment) {
                        val tokenData = token.paymentData
                        val jsonString = tokenData.toKString()

                        if (jsonString != null) {
                            onResult(
                                ApplePayResult.Success(
                                    tokenJson = jsonString,
                                    transactionIdentifier = token.transactionIdentifier
                                )
                            )
                        } else {
                            onResult(
                                ApplePayResult.Failure(
                                    errorCode = ApplePayErrorCode.TOKEN_EXTRACTION_FAILED
                                )
                            )
                        }
                    } else {
                        onResult(ApplePayResult.Cancelled)
                    }

                    this@KotlinNativeApplePayFactory.cleanup()
                }
            }
        }
    }
}

/**
 * Extension to convert NSData to String
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun platform.Foundation.NSData.toKString(): String? {
    return platform.Foundation.NSString.create(
        data = this,
        encoding = platform.Foundation.NSUTF8StringEncoding
    )?.toString()
}
