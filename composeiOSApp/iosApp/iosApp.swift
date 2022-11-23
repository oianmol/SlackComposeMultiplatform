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
    
     func application(
        _ application: UIApplication,
        supportedInterfaceOrientationsFor supportedInterfaceOrientationsForWindow: UIWindow?
     ) -> UIInterfaceOrientationMask {
         return UIInterfaceOrientationMask.all
    }

}
