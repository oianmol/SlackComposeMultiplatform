import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.*

object PainterRes {
    internal fun gettingStarted(): Painter {
        return painterResource("images/gettingstarted.png")
    }

    @Composable
    internal fun slackLogo(): Painter {
        return painterResource("images/ic_launcher_foreground.png")
    }
}
