package dev.baseio.slackclone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import kotlinx.cinterop.useContents
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow

@Composable
internal actual fun rememberComposeWindow(): State<WindowInfo> {
    val window = UIApplication.sharedApplication.windows.first() as UIWindow

    val rememberedComposeWindow = remember(window) {
        val windowInfo = window.frame.useContents {
            WindowInfo(this.size.width.dp, this.size.height.dp)
        }
        mutableStateOf(windowInfo)
    }

    // TODO observe window size changes in ios, ipadOS ?

    return rememberedComposeWindow
}
