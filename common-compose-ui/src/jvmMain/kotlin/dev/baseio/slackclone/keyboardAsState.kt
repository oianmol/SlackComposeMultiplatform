package dev.baseio.slackclone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dev.baseio.slackclone.Keyboard

@Composable
internal actual fun keyboardAsState(): State<Keyboard> {
    val keyboardState = remember { mutableStateOf<Keyboard>(Keyboard.HardwareKeyboard) }
    return keyboardState
}
