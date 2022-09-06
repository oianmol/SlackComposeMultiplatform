import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.WindowInfo
import dev.baseio.slackclone.appNavigator
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import dev.baseio.slackclone.data.DriverFactory
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import androidx.compose.ui.unit.dp
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
      this.window.isMinimized = true
    }

    Content(rememberedComposeWindow)
  }
}

@Composable
@ExperimentalComposeUiApi
private fun Content(rememberedComposeWindow: WindowInfo) {
  SlackCloneTheme {
    CompositionLocalProvider(
      LocalWindow provides rememberedComposeWindow
    ) {
      Scaffold(floatingActionButton = {
        FloatingActionButton()
      }, isFloatingActionButtonDocked = true) {
        App(sqlDriver = DriverFactory().createDriver())
      }
    }
  }
}

@ExperimentalComposeUiApi
@Composable
private fun FloatingActionButton() {
  var enter by remember { mutableStateOf(false) }
  val size by animateDpAsState(if(enter) 36.dp else 8.dp)
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