package dev.baseio.slackclone.navigation

import androidx.compose.runtime.Composable

@Composable
fun Navigator(
  navigator: ComposeNavigator,
  initialRoute: BackstackRoute,
  content: @Composable (ComposeNavigator.() -> Unit),
) {
  content(navigator)
  navigator.presentRoute(initialRoute)
}

@Composable
fun ComposeNavigator.screen(screenTag: BackstackScreen, content: @Composable BackstackScreen.() -> Unit) {
  this.registerScreen(screenTag, content)
}