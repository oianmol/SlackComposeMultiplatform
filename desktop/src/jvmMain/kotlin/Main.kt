import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerMoveFilter
import dev.baseio.slackclone.App
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.WindowInfo
import dev.baseio.slackclone.appNavigator
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.FabPosition
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.*
import dev.baseio.database.SlackDB
import dev.baseio.slackdata.DriverFactory
import dev.baseio.slackdata.SKKeyValueData
import kotlinx.coroutines.delay

@ExperimentalComposeUiApi
fun main() = application {
  val windowState = rememberWindowState()

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

    appNavigator.whenRouteCanNoLongerNavigateBack = {
      this@application.exitApplication()
    }

    SlackCloneTheme {
      Content(rememberedComposeWindow)
    }

  }
}

@Composable
@ExperimentalComposeUiApi
private fun FrameWindowScope.AppWindowTitleBar(windowState: WindowState, applicationScope: ApplicationScope) =
  WindowDraggableArea {
    Box(Modifier.fillMaxWidth().height(24.dp).background(Color.DarkGray)) {
      Row {
        CommonCircleShape(Modifier.clickable {
          applicationScope.exitApplication()
        }, Color.Red)
        CommonCircleShape(Modifier.clickable {
          this@AppWindowTitleBar.window.isMinimized = true
        }, Color.Yellow)
        CommonCircleShape(Modifier.clickable {
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
        }, Color.Green)
      }
    }
  }

@ExperimentalComposeUiApi
@Composable
private fun CommonCircleShape(modifier: Modifier, color: Color) {
  var enter by remember { mutableStateOf(false) }
  val alpha by animateFloatAsState(if (enter) 0.5f else 1f)
  Box(modifier.padding(4.dp).clip(CircleShape).size(16.dp).background(color.copy(alpha = alpha))
    .onPointerEvent(PointerEventType.Enter) {
      enter = true
    }
    .onPointerEvent(
      PointerEventType.Exit
    ) {
      enter = false
    })
}

@Composable
@ExperimentalComposeUiApi
private fun Content(rememberedComposeWindow: WindowInfo) {
  CompositionLocalProvider(
    LocalWindow provides rememberedComposeWindow
  ) {
    Scaffold(floatingActionButton = {
      FloatingActionButton()
    }, isFloatingActionButtonDocked = true, floatingActionButtonPosition = FabPosition.Center) {
      App(sqlDriver = DriverFactory().createDriver(SlackDB.Schema), skKeyValueData = SKKeyValueData())
    }
  }
}

@ExperimentalComposeUiApi
@Composable
private fun FloatingActionButton() {
  var enter by remember { mutableStateOf(true) }
  val size by animateDpAsState(if (enter) 36.dp else 8.dp)

  LaunchedEffect(true) {
    delay(700)
    enter = false
  }

  FloatingActionButton(modifier = Modifier.size(size).onPointerEvent(PointerEventType.Enter) {
    enter = true
  }.onPointerEvent(PointerEventType.Exit) {
    enter = false
  }, onClick = {
    appNavigator.navigateUp()
  }, content = {
    Icon(
      Icons.Default.ArrowBack,
      contentDescription = null,
      tint = SlackCloneColorProvider.colors.appBarIconColor
    )
  }, backgroundColor = SlackCloneColorProvider.colors.appBarColor)
}