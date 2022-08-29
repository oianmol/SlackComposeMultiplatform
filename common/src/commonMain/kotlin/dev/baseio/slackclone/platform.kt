package dev.baseio.slackclone

import androidx.compose.runtime.compositionLocalOf

data class WindowInfo(val width:Int,val height:Int)
val LocalWindow = compositionLocalOf<WindowInfo> { error("not available") }