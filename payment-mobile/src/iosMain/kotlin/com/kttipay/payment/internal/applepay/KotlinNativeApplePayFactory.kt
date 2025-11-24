package com.kttipay.payment.internal.applepay

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import org.kimplify.cedar.logging.Cedar
import platform.Foundation.NSDecimalNumber
import platform.Foundation.create
import platform.PassKit.*
import platform.UIKit.UIApplication
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/**
 * Pure Kotlin/Native implementation of ApplePayFactory using PassKit.
 *
 * This implementation uses Kotlin/Native's direct interop with iOS frameworks,
 * requiring no Swift or Objective-C code.
 */
@OptIn(ExperimentalForeignApi::class)
class KotlinNativeApplePayFactory : ApplePayFactory {

    private val cedar = Cedar.tag("ApplePayFactory")

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
        val canSetup = PKPaymentAuthorizationController.canMakePaymentsUsingNetworks(defaultNetworks)

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

            merchantCapabilities = if (request.merchantCapabilities3DS) {
                PKMerchantCapability3DS or PKMerchantCapabilityDebit or PKMerchantCapabilityCredit
            } else {
                PKMerchantCapabilityDebit or PKMerchantCapabilityCredit
            }

            supportedNetworks = request.supportedNetworks.mapNotNull { network ->
                when (network.lowercase()) {
                    "amex" -> PKPaymentNetworkAmex
                    "discover" -> PKPaymentNetworkDiscover
                    "mastercard" -> PKPaymentNetworkMasterCard
                    "visa" -> PKPaymentNetworkVisa
                    "jcb" -> PKPaymentNetworkJCB
                    else -> null
                }
            }

            paymentSummaryItems = request.summaryItems.map { item ->
                PKPaymentSummaryItem.summaryItemWithLabel(
                    label = item.label,
                    amount = NSDecimalNumber(item.amount.toString()),
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

        cedar.d("[ApplePay] Using merchantId=${request.merchantId}")

        controller.presentWithCompletion { presented ->
            if (!presented) {
                onResult(ApplePayResult.Failure(
                    message = "Failed to present Apple Pay",
                    errorCode = "present_failed"
                ))
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

        override fun paymentAuthorizationControllerWillAuthorizePayment(
            controller: PKPaymentAuthorizationController
        ) {
            cedar.d("[ApplePay] Will authorize payment (sheet about to ask FaceID/TouchID/passcode)")
        }

        override fun paymentAuthorizationController(
            controller: PKPaymentAuthorizationController,
            didAuthorizePayment: PKPayment,
            handler: (PKPaymentAuthorizationResult?) -> Unit
        ) {
            cedar.d("[ApplePay] Payment authorized, storing token")
            paymentToken = didAuthorizePayment.token
            hasCompletedPayment = true

            val result = PKPaymentAuthorizationResult(
                status = PKPaymentAuthorizationStatus.PKPaymentAuthorizationStatusSuccess,
                errors = null
            )
            handler(result)
            cedar.d("[ApplePay] Authorization result sent to Apple Pay")
        }

        override fun paymentAuthorizationControllerDidFinish(
            controller: PKPaymentAuthorizationController
        ) {
            cedar.d("[ApplePay] Payment authorization finished, dismissing...")

            controller.dismissWithCompletion {
                cedar.d("[ApplePay] Dismiss completed, processing result on main queue...")

                dispatch_async(dispatch_get_main_queue()) {
                    cedar.d("[ApplePay] On main queue, checking token...")
                    val token = paymentToken

                    if (token != null && hasCompletedPayment) {
                        cedar.d("[ApplePay] Processing successful payment token")
                        val tokenData = token.paymentData
                        val jsonString = tokenData.toKString()

                        if (jsonString != null) {
                            cedar.d("[ApplePay] Token extracted successfully (length: ${jsonString.length}), calling onResult")
                            onResult(ApplePayResult.Success(
                                tokenJson = jsonString,
                                transactionIdentifier = token.transactionIdentifier
                            ))
                        } else {
                            cedar.d("[ApplePay] ERROR: Failed to convert token data to string")
                            onResult(ApplePayResult.Failure(
                                message = "Failed to extract payment token",
                                errorCode = "token_extraction_failed"
                            ))
                        }
                    } else {
                        // User cancelled or no payment was authorized
                        cedar.d("[ApplePay] Payment was cancelled by user (token: ${token != null}, completed: $hasCompletedPayment)")
                        onResult(ApplePayResult.Cancelled)
                    }

                    cedar.d("[ApplePay] Cleaning up resources")
                    // Cleanup on the outer class, not just the delegate
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