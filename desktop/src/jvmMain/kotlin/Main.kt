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
import dev.baseio.database.SlackDB
import dev.baseio.slackclone.App
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.WindowInfo
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import dev.baseio.slackclone.initKoin
import dev.baseio.slackdata.DriverFactory
import dev.baseio.slackdata.SKKeyValueData
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@ExperimentalComposeUiApi
fun main() = application {
    JVMSecurityUtils.initialize()

    val windowState = rememberWindowState()
    val lifecycle = LifecycleRegistry()

    val skKeyValueData = SKKeyValueData()
    val rootComponent by lazy { RootComponent(DefaultComponentContext(lifecycle = lifecycle)) }
    val koinApplication =
    initKoin({ skKeyValueData }, { DriverFactory().createDriver(SlackDB.Schema) })

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

        DesktopApp(rememberedComposeWindow, {
            rootComponent
        }, koinApplication)
    }
}


@Composable
fun DesktopApp(
    rememberedComposeWindow: WindowInfo,
    rootComponent: () -> RootComponent,
    koinApplication: org.koin.core.KoinApplication
) {
    SlackCloneTheme {
        CompositionLocalProvider(
            LocalWindow provides rememberedComposeWindow
        ) {
            App(
                rootComponent = rootComponent,
                koinApplication = koinApplication
            )
        }
    }
}


@Composable
@ExperimentalComposeUiApi
private fun FrameWindowScope.AppWindowTitleBar(windowState: WindowState, applicationScope: ApplicationScope) =
    WindowDraggableArea {
        Box(Modifier.fillMaxWidth().height(24.dp).background(Color.DarkGray)) {
            Row {
                CommonCircleShape(
                    Modifier.clickable {
                        applicationScope.exitApplication()
                    },
                    Color.Red
                )
                CommonCircleShape(
                    Modifier.clickable {
                        this@AppWindowTitleBar.window.isMinimized = true
                    },
                    Color.Yellow
                )
                CommonCircleShape(
                    Modifier.clickable {
                        when (windowState.placement) {
                            WindowPlacement.Fullscreen -> {
                                windowState.placement = WindowPlacement.Floating
                            }

                            WindowPlacement.Floating -> {
                                windowState.placement = WindowPlacement.Fullscreen
                            }

                            WindowPlacement.Maximized -> {
                                windowState.placement = WindowPlacement.Floating
                            }
                        }
                    },
                    Color.Green
                )
            }
        }
    }

@ExperimentalComposeUiApi
@Composable
private fun CommonCircleShape(modifier: Modifier, color: Color) {
    var enter by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(if (enter) 0.5f else 1f)
    Box(
        modifier.padding(4.dp).clip(CircleShape).size(16.dp).background(color.copy(alpha = alpha))
            .onPointerEvent(PointerEventType.Enter) {
                enter = true
            }
            .onPointerEvent(
                PointerEventType.Exit
            ) {
                enter = false
            }
    )
}
