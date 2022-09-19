package dev.baseio.slackclone.uionboarding.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

expect object PlatformSideEffects {
  @Composable
  fun GettingStartedScreen()

  @Composable
  fun SkipTypingScreen()
  @Composable
  fun PlatformColors(topColor: Color, bottomColor: Color)
}