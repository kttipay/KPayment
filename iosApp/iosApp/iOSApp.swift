import SwiftUI

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

// Note: Apple Pay is automatically initialized by the payment-mobile library
// No manual factory registration required!