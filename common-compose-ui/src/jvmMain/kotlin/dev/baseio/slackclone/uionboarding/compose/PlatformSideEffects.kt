package dev.baseio.slackclone.uionboarding.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

actual object PlatformSideEffects {
    @Composable
    internal actual fun GettingStartedScreen() {
    }

    @Composable
    internal actual fun SkipTypingScreen() {
    }

    @Composable
    internal actual fun PlatformColors(
        topColor: Color,
        bottomColor: Color
    ) {
    }
}
