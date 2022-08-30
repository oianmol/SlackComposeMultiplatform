import androidx.compose.runtime.*
import dev.baseio.slackclone.App
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.WindowInfo
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import dev.baseio.slackclone.data.DriverFactory
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun main() = application {
  val windowState = rememberWindowState()
  Window(onCloseRequest = ::exitApplication, state = windowState) {
    var rememberedComposeWindow by remember(this.window) {
      mutableStateOf(WindowInfo(windowState.size.width, windowState.size.height))
    }

    LaunchedEffect(windowState) {
      snapshotFlow { windowState.size }
        .distinctUntilChanged()
        .onEach {
          rememberedComposeWindow = WindowInfo(it.width, it.height)
        }
        .launchIn(this)
    }

    SlackCloneTheme {
      CompositionLocalProvider(
        LocalWindow provides rememberedComposeWindow
      ) {
        App(sqlDriver = DriverFactory().createDriver())
      }
    }
  }
}