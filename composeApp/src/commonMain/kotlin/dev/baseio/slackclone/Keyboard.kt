package dev.baseio.slackclone

sealed class Keyboard {
    data class Opened(var height: Int) : Keyboard()
    object Closed : Keyboard()
    object HardwareKeyboard : Keyboard()
}
