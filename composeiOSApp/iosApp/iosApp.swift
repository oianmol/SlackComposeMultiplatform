import UIKit
import commoncomposeui

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    var myWindow: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        myWindow = UIWindow(frame: UIScreen.main.bounds)
            let mainViewController = Main_iosKt.MainViewController(window: myWindow!)
        myWindow?.rootViewController = mainViewController
        myWindow?.makeKeyAndVisible()
            return true
        }
    
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
