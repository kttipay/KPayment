//
//  ComposeHostContainer.swift
//  iosApp
//
//  Created for KPayment Sample App
//  Copyright © 2025 orgName. All rights reserved.
//

import UIKit

/**
 * Container to hold a weak reference to the presenting UIViewController.
 *
 * This is used by ApplePayFactoryImpl to present the PKPaymentAuthorizationController
 * from the correct view controller context.
 *
 * The viewController is set when the Compose MainViewController is created in ContentView.
 */
class ComposeHostContainer {
    /// Weak reference to the presenting view controller
    static weak var viewController: UIViewController?
}
