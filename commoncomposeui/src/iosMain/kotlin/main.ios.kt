import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import dev.baseio.slackclone.*
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import platform.UIKit.UIViewController

val lifecycle = LifecycleRegistry()
val rootComponent by lazy {
    RootComponent(
        context = DefaultComponentContext(lifecycle = lifecycle),
    )
}

fun MainViewController(): UIViewController =
    ComposeUIViewController {
        initKoin()

        val rememberedComposeWindow by rememberComposeWindow()

        CompositionLocalProvider(
            LocalWindow provides rememberedComposeWindow
        ) {
            SlackCloneTheme(isDarkTheme = true) {
                SlackApp {
                    rootComponent
                }
            }
        }
    }
