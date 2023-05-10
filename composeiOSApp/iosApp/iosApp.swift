import UIKit
import SwiftUI
import commoncomposeui

let gradient = LinearGradient(
        colors: [
            Color.black.opacity(0.6),
            Color.black.opacity(0.6),
            Color.black.opacity(0.5),
            Color.black.opacity(0.3),
            Color.black.opacity(0.0),
        ],
        startPoint: .top, endPoint: .bottom
)

@main
struct iOSApp: App {

	var body: some Scene {
		WindowGroup {
			ContentView().onOpenURL { url in
                print(url.absoluteString)
                if let scheme = url.scheme,
                    scheme.localizedCaseInsensitiveCompare("slackclone") == .orderedSame,
                    let _ = url.host {
                    
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
        let controller = Main_iosKt.MainViewController()
        controller.overrideUserInterfaceStyle = .light
        return controller
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ZStack {
            ComposeView()
                    .ignoresSafeArea(.all) // Compose has own keyboard handler
            VStack {
                gradient.ignoresSafeArea(edges: .top).frame(height: 0)
                Spacer()
            }
        }.preferredColorScheme(.dark)
    }
}

