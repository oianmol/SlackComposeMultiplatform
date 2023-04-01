import UIKit
import SwiftUI
import commoncomposeui

@main
struct iOSApp: App {

	var body: some Scene {
		WindowGroup {
			ContentView().onOpenURL { url in
                print(url.absoluteString)
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
            }
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
