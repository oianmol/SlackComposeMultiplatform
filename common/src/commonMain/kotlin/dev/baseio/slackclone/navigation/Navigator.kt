package dev.baseio.slackclone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun Navigator(
  navigator: ComposeNavigator,
  initialRoute: BackstackRoute,
  composeNavigatorComposable: @Composable (ComposeNavigator.() -> Unit),
) {
  composeNavigatorComposable(remember { navigator })
  navigator.start(initialRoute)
}

@Composable
fun ComposeNavigator.screen(screenTag: BackstackScreen, content: @Composable () -> Unit) {
  this.registerScreen(screenTag, content)
}