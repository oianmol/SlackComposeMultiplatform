package dev.baseio.slackclone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun Navigator(
  initialScreen: BackstackScreen,
  navigator: ComposeNavigator = remember { SlackComposeNavigator(initialScreen) },
  composeNavigatorComposable: @Composable ComposeNavigator.() -> Unit
) {
  composeNavigatorComposable(navigator)
  navigator.start()
}

fun ComposeNavigator.screen(screenTag: BackstackScreen, content: @Composable () -> Unit) {
  this.registerScreen(screenTag, content)
}