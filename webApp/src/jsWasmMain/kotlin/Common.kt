import androidx.compose.runtime.*
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.SlackApp
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import dev.baseio.slackclone.initKoin
import dev.baseio.slackclone.rememberComposeWindow

@Composable
internal fun SlackWebApp() {
    initKoin()
    val lifecycle = LifecycleRegistry()
    val rootComponent by lazy { RootComponent(DefaultComponentContext(lifecycle = lifecycle)) }
    val rememberedComposeWindow by rememberComposeWindow()
    SlackCloneTheme {
        CompositionLocalProvider(
            LocalWindow provides rememberedComposeWindow
        ) {
            SlackApp(
                rootComponent = {
                    rootComponent
                }
            )
        }
    }
}