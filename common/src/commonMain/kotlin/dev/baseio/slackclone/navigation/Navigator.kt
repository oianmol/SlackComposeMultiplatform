package dev.baseio.slackclone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun Navigator(initialScreen: BackstackScreen, composeNavigatorComposable: @Composable ComposeNavigator.() -> Unit) {
    val navigator = remember { SlackComposeNavigator(initialScreen) }
    composeNavigatorComposable(navigator)
    navigator.start()
}

fun ComposeNavigator.screen(screenTag: BackstackScreen, content: @Composable () -> Unit) {
    this.registerScreen(screenTag, content)
}