package dev.baseio.slackclone.onboarding.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

expect object PlatformSideEffects {
    @Composable
    internal fun GettingStartedScreen()

    @Composable
    internal fun SkipTypingScreen()

    @Composable
    internal fun PlatformColors(topColor: Color, bottomColor: Color)
}
