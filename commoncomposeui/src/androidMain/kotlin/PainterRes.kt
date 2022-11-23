import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import dev.baseio.composeui.R

actual object PainterRes {
    @Composable
    internal actual fun gettingStarted() = painterResource(R.drawable.gettingstarted)

    @Composable
    internal actual fun slackLogo() = painterResource(R.drawable.ic_launcher_foreground)
}
