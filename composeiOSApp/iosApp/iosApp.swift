import UIKit
import SwiftUI
import commoncomposeui

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}


class AppDelegate: UIResponder, UIApplicationDelegate {
    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        if let scheme = url.scheme,
            scheme.localizedCaseInsensitiveCompare("slackclone") == .orderedSame,
            let view = url.host {
            
            var parameters: [String: String] = [:]
            URLComponents(url: url, resolvingAgainstBaseURL: false)?.queryItems?.forEach {
                parameters[$0.name] = $0.value
            }
            if let token = parameters["token"] {
                if !token.isEmpty {
                    Main_iosKt.rootComponent.navigateAuthorizeWithToken(token: token)
                }
            }
        
        }
        return true
    }
    
     func application(
        _ application: UIApplication,
        supportedInterfaceOrientationsFor supportedInterfaceOrientationsForWindow: UIWindow?
     ) -> UIInterfaceOrientationMask {
         return UIInterfaceOrientationMask.all
    }

}
