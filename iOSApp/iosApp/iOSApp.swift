import SwiftUI
import common

@main
struct iOSApp: App {
    
    init() {
        KoinInitKt.doInitKoin()
    }
    
	var body: some Scene {
		WindowGroup {
            AuthCreateWorkspace()
		}
	}
}
