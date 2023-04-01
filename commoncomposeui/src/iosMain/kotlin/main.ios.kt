import androidx.compose.foundation.background // ktlint-disable import-ordering
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Application
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import dev.baseio.slackclone.SlackApp
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.WindowInfo
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import dev.baseio.slackclone.initKoin
import kotlinx.cinterop.useContents
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
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

        val window = UIApplication.sharedApplication.windows.first() as UIWindow

        val rememberedComposeWindow by remember(window) {
            val windowInfo = window.frame.useContents {
                WindowInfo(this.size.width.dp, this.size.height.dp)
            }
            mutableStateOf(windowInfo)
        }

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
