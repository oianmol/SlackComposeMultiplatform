package dev.baseio.slackclone.chatmessaging.chatthread

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.dashboard.compose.WindowSize
import dev.baseio.slackclone.dashboard.compose.getWindowSizeClass

@Composable
fun SecurityKeysRequestUI(viewModel: ChatViewModel) {
    BoxWithConstraints(
        modifier = Modifier
            .background(LocalSlackCloneColor.current.onUiBackground)
    ) {
        val (width, height) = when (getWindowSizeClass()) {
            WindowSize.Phones, WindowSize.SmallTablets -> Pair(maxWidth, maxHeight.times(0.2f))
            WindowSize.BigTablets, WindowSize.DesktopOne, WindowSize.DesktopTwo -> Pair(
                maxWidth.times(
                    0.4f
                ), maxHeight.times(
                    0.4f
                )
            )
        }
        Surface(
            modifier = Modifier.width(width)
                .height(height)
                .background(
                    color = LocalSlackCloneColor.current.uiBackground,
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
        ) {


        }
    }
}