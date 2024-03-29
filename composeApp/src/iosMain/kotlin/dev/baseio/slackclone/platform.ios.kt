@file:OptIn(ExperimentalForeignApi::class)

package dev.baseio.slackclone

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIApplication
import platform.UIKit.UIEdgeInsets

private val iosNotchInset = object : WindowInsets {
    override fun getTop(density: Density): Int {
        val safeAreaInsets: CValue<UIEdgeInsets>? = UIApplication.sharedApplication.keyWindow?.safeAreaInsets
        return if (safeAreaInsets != null) {
            val topInset = safeAreaInsets.useContents { this.top }
            (topInset * density.density).toInt()
        } else {
            0
        }
    }

    override fun getLeft(density: Density, layoutDirection: LayoutDirection): Int = 0
    override fun getRight(density: Density, layoutDirection: LayoutDirection): Int = 0
    override fun getBottom(density: Density): Int = 0
}

actual fun Modifier.notchPadding(): Modifier =
    this.windowInsetsPadding(iosNotchInset)

