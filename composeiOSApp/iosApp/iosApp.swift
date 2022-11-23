import UIKit
import common

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        window = UIWindow(frame: UIScreen.main.bounds)
        let mainViewController = Main_iosKt.MainViewController(window)
        window?.rootViewController = mainViewController
        window?.makeKeyAndVisible()
        return true
    }

     override fun applicationDidBecomeActive(application: UIApplication) {
            Main_iosKt.lifecycle.resume()
        }

        override fun applicationWillResignActive(application: UIApplication) {
              Main_iosKt.lifecycle.stop()
        }

        override fun applicationWillTerminate(application: UIApplication) {
              Main_iosKt.lifecycle.destroy()
        }
}
