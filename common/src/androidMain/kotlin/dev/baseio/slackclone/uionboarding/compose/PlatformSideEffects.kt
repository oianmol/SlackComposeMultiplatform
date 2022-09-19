package dev.baseio.slackclone.uionboarding.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.baseio.slackclone.commonui.theme.SlackCloneColor

actual object PlatformSideEffects {


  @Composable
  actual fun GettingStartedScreen() {
    val sysUiController = rememberSystemUiController()

    SideEffect {
      sysUiController.setNavigationBarColor(color = SlackCloneColor)
      sysUiController.setSystemBarsColor(color = SlackCloneColor)
    }
  }

  @Composable
  actual fun SkipTypingScreen() {
    val sysUiController = rememberSystemUiController()

    SideEffect {
      sysUiController.setNavigationBarColor(color = SlackCloneColor)
      sysUiController.setSystemBarsColor(color = SlackCloneColor)
    }
  }

  @Composable
  actual fun PlatformColors(
    topColor: Color,
    bottomColor: Color
  ) {
    val sysUiController = rememberSystemUiController()
    SideEffect {
      sysUiController.setSystemBarsColor(color = topColor)
      sysUiController.setNavigationBarColor(color = bottomColor)
    }
  }

}