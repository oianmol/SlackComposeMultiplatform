package dev.baseio.slackclone

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp

data class WindowInfo(val width: Dp, val height: Dp, val minDimen: Dp? = null, val maxDimen: Dp? = null)

internal val LocalWindow = compositionLocalOf { WindowInfo(Dp.Unspecified, Dp.Unspecified) }
