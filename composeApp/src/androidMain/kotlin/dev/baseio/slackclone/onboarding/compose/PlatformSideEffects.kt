package dev.baseio.slackclone.onboarding.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import dev.baseio.slackclone.commonui.theme.SlackCloneColor

actual object PlatformSideEffects {

    @Composable
    internal actual fun SlackCloneColorOnPlatformUI() {
    }

    @Composable
    internal actual fun PlatformColors(
        topColor: Color,
        bottomColor: Color
    ) {
        SideEffect {
        }
    }
}
