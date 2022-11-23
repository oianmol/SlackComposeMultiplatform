import SwiftUI
import common


struct ContentView: View {

    init() {
        KoinInitKt.doInitKoin()
    }
    
	var body: some View {
		Text("greet")
	}
}
