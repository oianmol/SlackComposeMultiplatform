import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import dev.baseio.slackclone.App
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme

fun main() = application {
  Window(onCloseRequest = ::exitApplication) {
    SlackCloneTheme {
      App()
    }
  }
}