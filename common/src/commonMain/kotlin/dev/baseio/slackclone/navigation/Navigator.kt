package dev.baseio.slackclone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun Navigator(initialScreen: BackstackScreen, navigatorComposable: @Composable Navigator.() -> Unit) {
    val navigator = remember { SlackNavigator(initialScreen) }
    navigatorComposable(navigator)
    navigator.start()
}

fun Navigator.screen(screenTag: BackstackScreen, content: @Composable () -> Unit) {
    this.registerScreen(screenTag, content)
}