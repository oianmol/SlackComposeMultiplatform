import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
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

@ExperimentalComposeUiApi
fun main() = application {
    val windowState = rememberWindowState()
    val lifecycle = LifecycleRegistry()

    val rootComponent by lazy { RootComponent(DefaultComponentContext(lifecycle = lifecycle)) }
    initKoin()

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


@Composable
internal fun DesktopApp(
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


