import androidx.compose.ui.window.ComposeUIViewController
import dev.oianmol.slack.App
import platform.UIKit.UIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.SlackApp
import dev.baseio.slackclone.initKoin

val lifecycle = LifecycleRegistry()
val rootComponent by lazy {
    RootComponent(
        context = DefaultComponentContext(lifecycle = lifecycle),
        )
}

fun MainViewController(): UIViewController = ComposeUIViewController {
    initKoin()
    SlackCloneTheme(isDarkTheme = true) {
        SlackApp {
            rootComponent
        }
    }
}
