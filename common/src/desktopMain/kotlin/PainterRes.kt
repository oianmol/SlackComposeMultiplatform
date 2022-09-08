import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

actual object PainterRes {
  @Composable
  actual fun gettingStarted(): Painter {
    return painterResource("images/gettingstarted.png")
  }
}