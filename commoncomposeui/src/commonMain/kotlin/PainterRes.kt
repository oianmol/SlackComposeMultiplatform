import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

expect object PainterRes {
    @Composable
    internal fun gettingStarted(): Painter

    @Composable
    internal fun slackLogo(): Painter
}
