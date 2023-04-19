import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import kotlin.wasm.unsafe.*

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow("SlackWebApp") {
        SlackWebApp()
    }
}
