import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

expect object PainterRes {
    @Composable
    fun gettingStarted(): Painter

    @Composable
    fun slackLogo(): Painter
}
