package dev.baseio.slackclone

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
actual fun rememberComposeWindow(): State<WindowInfo> {
    val config = LocalConfiguration.current

    val rememberedComposeWindow = remember {
        mutableStateOf(WindowInfo(config.screenWidthDp.dp, config.screenHeightDp.dp))
    }

    LaunchedEffect(config) {
        snapshotFlow { config }.distinctUntilChanged().onEach {
            rememberedComposeWindow.value = WindowInfo(it.screenWidthDp.dp, it.screenHeightDp.dp)
        }.launchIn(this)
    }

    return rememberedComposeWindow
}
