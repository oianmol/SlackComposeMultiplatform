package dev

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
actual fun keyboardAsState(): State<Keyboard> {
    val keyboardState = remember { mutableStateOf<Keyboard>(Keyboard.HardwareKeyboard) }
    return keyboardState
}
