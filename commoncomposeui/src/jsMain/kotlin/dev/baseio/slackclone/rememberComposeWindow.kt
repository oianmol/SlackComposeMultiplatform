package dev.baseio.slackclone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.events.Event

@Composable
actual fun rememberComposeWindow(): State<WindowInfo> {
    val height = document.querySelector("#height span")
    val width = document.querySelector("#width span")

    val rememberedComposeWindow = remember {
        mutableStateOf(WindowInfo(window.innerWidth.dp, window.innerWidth.dp))
    }
    val onChange: (Event) -> Unit = {
        height?.innerHTML = window.innerHeight.toString()
        window.innerWidth.toString().also { width?.innerHTML = it }
        rememberedComposeWindow.value = WindowInfo(window.innerWidth.dp, window.innerWidth.dp)
    }

    // Insert values on load of page
    window.onload = onChange
    // Change values when window is resized
    window.onresize = onChange

    return rememberedComposeWindow
}