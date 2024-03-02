@file:OptIn(ExperimentalForeignApi::class)

package dev.baseio.slackclone.onboarding.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import dev.baseio.slackclone.commonui.theme.SlackCloneColor
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.zeroValue
import platform.CoreGraphics.CGRect
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UINavigationBar
import platform.UIKit.UIView
import platform.UIKit.UIWindow
import platform.UIKit.statusBarManager

actual object PlatformSideEffects {
    @Composable
    internal actual fun SlackCloneColorOnPlatformUI() {
        val statusBar = uiView()
        SideEffect {
            with(SlackCloneColor.toUIColor()) {
                statusBar.backgroundColor = this
                UINavigationBar.appearance().backgroundColor = this
            }
        }
    }

    @Composable
    private fun uiView() = remember {
        val keyWindow: UIWindow? =
            UIApplication.sharedApplication.windows.firstOrNull { (it as? UIWindow)?.isKeyWindow() == true } as? UIWindow
        val tag = 3848245L

        keyWindow?.viewWithTag(tag)?.let {
            it
        } ?: run {
            val height =
                keyWindow?.windowScene?.statusBarManager?.statusBarFrame ?: zeroValue<CGRect>()
            val statusBarView = UIView(frame = height)
            statusBarView.tag = tag
            statusBarView.layer.zPosition = 999999.0
            keyWindow?.addSubview(statusBarView)
            statusBarView
        }
    }

    @Composable
    internal actual fun PlatformColors(
        topColor: Color,
        bottomColor: Color
    ) {
        val statusBar = uiView()
        SideEffect {
            statusBar.backgroundColor = topColor.toUIColor()
            UINavigationBar.appearance().backgroundColor = bottomColor.toUIColor()
        }
    }
}

private fun Color.toUIColor(): UIColor =
    UIColor(
        red = this.red.toDouble(),
        green = this.green.toDouble(),
        blue = this.blue.toDouble(),
        alpha = this.alpha.toDouble()
    )