import UIKit
import SwiftUI
import ComposeApp

@main
struct iosApp: App {
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
                                MainKt.rootComponent.navigateAuthorizeWithToken(token: token)
                            }
                        }
                }
            }
        }
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView().ignoresSafeArea(.all)
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
