import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

actual object PainterRes {
    @Composable
    internal actual fun gettingStarted(): Painter {
        return painterResource("images/gettingstarted.png")
    }

    @Composable
    internal actual fun slackLogo(): Painter {
        return painterResource("images/ic_launcher_foreground.png")
    }
}
