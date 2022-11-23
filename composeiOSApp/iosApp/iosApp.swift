import UIKit
import commoncomposeui

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        window = UIWindow(frame: UIScreen.main.bounds)
        let mainViewController = Main_iosKt.MainViewController(window: window!)
        window?.rootViewController = mainViewController
        window?.makeKeyAndVisible()
        return true
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
            //Main_iosKt.lifecycle.resume()
        }

    func applicationWillResignActive(_ application: UIApplication) {
             // Main_iosKt.lifecycle.stop()
        }

    func applicationWillTerminate(_ application: UIApplication) {
             // Main_iosKt.lifecycle.destroy()
        }
}
