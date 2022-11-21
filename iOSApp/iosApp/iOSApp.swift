import SwiftUI
import slack_data_layer
import slack_domain_layer
import common

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    init() {
        
    }

	var body: some Scene {
		WindowGroup {
            SomeView()
		}
	}
}


struct SomeView : View{

    var body: some View{
        VStack{
            
        }
    }
}
