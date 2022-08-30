import dev.baseio.slackclone.App
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.baseio.database.SlackDB
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.WindowInfo
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import dev.baseio.slackclone.data.DriverFactory

fun main() = application {
  Window(onCloseRequest = ::exitApplication) {
    val rememberedComposeWindow = remember {
      WindowInfo(this.window.width, this.window.height)
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