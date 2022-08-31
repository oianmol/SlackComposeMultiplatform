package dev.baseio.slackclone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import java.util.*
import kotlin.collections.LinkedHashMap

@Composable
fun Navigator(
  navigator: ComposeNavigator,
  composeNavigatorComposable: @Composable ComposeNavigator.() -> Unit
) {
  composeNavigatorComposable(remember { navigator })
  navigator.start()
}


fun ComposeNavigator.screen(screenTag: BackstackScreen, content: @Composable () -> Unit) {
  this.registerScreen(screenTag, content)
}