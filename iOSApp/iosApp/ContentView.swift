import SwiftUI
import common

struct ContentView: View {

    init() {
        KoinInitKt.doInitKoin()
        AuthKoinComponents().provideUseCaseCreateWorkspace().invoke(email: "anmol.verma4@gmail.com", password: "password", domain: "mutualmobileios") { error in
            debugPrint(error ?? "no error")
        }
    }
    
	var body: some View {
		Text("greet")
	}
}
