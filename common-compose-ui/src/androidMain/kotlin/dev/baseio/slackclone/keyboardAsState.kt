package dev.baseio.slackclone

import android.content.res.Configuration // ktlint-disable import-ordering
import android.content.res.Resources
import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import dev.baseio.slackclone.Keyboard

@Composable
actual fun keyboardAsState(): State<Keyboard> {
    val resources = LocalContext.current.resources
    val keyboardState =
        remember { mutableStateOf(if (isHardwareKeyboardAvailable(resources)) Keyboard.HardwareKeyboard else Keyboard.Closed) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardState.value = if (keypadHeight > screenHeight * 0.15) {
                Keyboard.Opened(screenHeight - keypadHeight)
            } else {
                Keyboard.Closed
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}

private fun isHardwareKeyboardAvailable(resources: Resources): Boolean {
    return resources.configuration.keyboard != Configuration.KEYBOARD_NOKEYS
}
