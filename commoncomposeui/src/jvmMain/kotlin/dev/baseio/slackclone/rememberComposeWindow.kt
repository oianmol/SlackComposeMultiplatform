package dev.baseio.slackclone

import androidx.compose.runtime.*
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
actual fun rememberComposeWindow(): State<WindowInfo> {
    val windowState = rememberWindowState()
    val rememberedComposeWindow = remember(windowState) {
        mutableStateOf(WindowInfo(windowState.size.width, windowState.size.height))
    }

    LaunchedEffect(windowState) {
        snapshotFlow { windowState.size }
            .distinctUntilChanged()
            .onEach {
                rememberedComposeWindow.value = WindowInfo(width = it.width, height = it.height)
            }
            .launchIn(this)
    }
    return rememberedComposeWindow
}