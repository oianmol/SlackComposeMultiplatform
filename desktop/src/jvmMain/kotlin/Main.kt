import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberNotification
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import dev.baseio.slackclone.SlackApp
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.WindowInfo
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import dev.baseio.slackclone.initKoin
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.awt.Desktop
import kotlin.io.path.toPath

@ExperimentalComposeUiApi
fun main() {
    application {
        val windowState = rememberWindowState()
        val lifecycle = LifecycleRegistry()


        val rootComponent by lazy { RootComponent(DefaultComponentContext(lifecycle = lifecycle)) }
        initKoin()

        handleDeepLink(rootComponent)

        Window(onCloseRequest = ::exitApplication, state = windowState) {
            var rememberedComposeWindow by remember(this.window) {
                mutableStateOf(WindowInfo(windowState.size.width, windowState.size.height))
            }

            LaunchedEffect(windowState) {
                snapshotFlow { windowState.size }
                    .distinctUntilChanged()
                    .onEach {
                        rememberedComposeWindow = WindowInfo(it.width, it.height)
                    }
                    .launchIn(this)
            }

            DesktopApp(rememberedComposeWindow) {
                rootComponent
            }
        }
    }
}

private fun handleDeepLink(rootComponent: RootComponent) {
    try {
        Desktop.getDesktop().setOpenURIHandler { event ->
            val queryMap = UriUtil.parseQueryMap(event.uri)
            queryMap["channelId"]?.let { channelId ->
                queryMap["workspaceId"]?.let { workspaceId ->
                    rootComponent.navigateChannel(
                        channelId,
                        workspaceId
                    )
                }
            }
            queryMap["token"]?.let { token ->
                rootComponent.navigateAuthorizeWithToken(token)
            }
        }
    } catch (e: UnsupportedOperationException) {
        println("setOpenURIHandler is unsupported")
    }
}

@Composable
fun DesktopApp(
    rememberedComposeWindow: WindowInfo,
    rootComponent: () -> RootComponent
) {
    SlackCloneTheme {
        CompositionLocalProvider(
            LocalWindow provides rememberedComposeWindow
        ) {
            SlackApp(
                rootComponent = rootComponent
            )
        }
    }
}


