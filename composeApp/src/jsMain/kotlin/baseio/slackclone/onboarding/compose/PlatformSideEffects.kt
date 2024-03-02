package dev.baseio.slackclone.onboarding.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

actual object PlatformSideEffects {
    @Composable
    internal actual fun SlackCloneColorOnPlatformUI() {
    }

    @Composable
    internal actual fun PlatformColors(
        topColor: Color,
        bottomColor: Color
    ) {
    }
}
