//
//  ApplePayFactoryImpl.swift
//  payment-mobile
//
//  Default Apple Pay PassKit implementation for KPayment library
//  Copyright © 2025 KTTIPAY PTY LTD. All rights reserved.
//

import PassKit
import UIKit
import PaymentMobile

/**
 * Default implementation of ApplePayFactory using PassKit.
 *
 * This implementation is automatically used by the payment-mobile library,
 * so apps don't need to provide their own implementation unless they want
 * custom behavior.
 */
@objc public class ApplePayFactoryImpl: NSObject, ApplePayFactory {

    private weak var hostVC: UIViewController?
    private var completion: ((ApplePayResult) -> Void)?
    private var lastToken: PKPaymentToken?
    private static let defaultNetworks: [PKPaymentNetwork] = [.amex, .discover, .masterCard, .visa, .JCB]

    public func applePayStatus() -> ApplePayStatus {
        let canPay = PKPaymentAuthorizationController.canMakePayments()
        let canSetup = PKPaymentAuthorizationController.canMakePayments(usingNetworks: Self.defaultNetworks)
        return ApplePayStatus(canMakePayments: canPay, canSetupCards: canSetup)
    }

    public func startPayment(request: ApplePayRequest, onResult: @escaping (ApplePayResult) -> Void) {
        // Get view controller from the provider
        guard let host = ApplePayViewControllerProviderHolder.shared.getCurrentViewController() else {
            onResult(ApplePayResultFailure(message: "No view controller available for presenting Apple Pay", errorCode: "no_vc"))
            return
        }

        let pk = PKPaymentRequest()
        pk.merchantIdentifier = request.merchantId
        pk.countryCode = request.countryCode
        pk.currencyCode = request.currencyCode
        pk.merchantCapabilities = [.capability3DS, .debit]
        pk.supportedNetworks = Self.defaultNetworks

        pk.paymentSummaryItems = request.summaryItems.map {
            PKPaymentSummaryItem(
                label: $0.label,
                amount: NSDecimalNumber(string: $0.amount.description),
                type: $0.isFinal ? .final : .pending
            )
        }

        if #available(iOS 15.0, *) {
            pk.supportsCouponCode = request.supportsCouponCode
            if let code = request.initialCouponCode {
                pk.couponCode = code
            }
        }

        let controller = PKPaymentAuthorizationController(paymentRequest: pk)
        controller.delegate = self
        self.hostVC = host
        self.completion = onResult

        controller.present { presented in
            if !presented {
                onResult(ApplePayResultFailure(message: "Failed to present Apple Pay", errorCode: "present_failed"))
                self.cleanup()
            }
        }
    }

    public func presentSetupFlow(onFinished: @escaping (KotlinBoolean) -> Void) {
        DispatchQueue.main.async {
            PKPassLibrary().openPaymentSetup()
        }
        onFinished(KotlinBoolean(true))
    }

    private func cleanup() {
        completion = nil
        lastToken = nil
        hostVC = nil
    }
}

extension ApplePayFactoryImpl: PKPaymentAuthorizationControllerDelegate {
    public func paymentAuthorizationController(
        _: PKPaymentAuthorizationController,
        didAuthorizePayment payment: PKPayment,
        handler completionHandler: @escaping (PKPaymentAuthorizationResult) -> Void
    ) {
        lastToken = payment.token
        completionHandler(PKPaymentAuthorizationResult(status: .success, errors: []))
    }

    public func paymentAuthorizationControllerDidFinish(_ controller: PKPaymentAuthorizationController) {
        controller.dismiss { [weak self] in
            guard let self = self else { return }
            guard let callback = self.completion else { return }

            DispatchQueue.main.async {
                if let token = self.lastToken,
                   let json = String(data: token.paymentData, encoding: .utf8) {
                    callback(ApplePayResultSuccess(
                        tokenJson: json,
                        transactionIdentifier: token.transactionIdentifier
                    ))
                } else {
                    callback(ApplePayResultCancelled.shared)
                }
                self.cleanup()
            }
        }
    }
}
