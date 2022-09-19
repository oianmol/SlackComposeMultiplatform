package dev.baseio.slackclone.uionboarding.compose

import androidx.compose.runtime.*
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import dev.baseio.slackclone.navigation.ComposeNavigator

@Composable
fun EmailAddressInputUI(composeNavigator: ComposeNavigator) {
  val colors = SlackCloneColorProvider.colors
  PlatformSideEffects.PlatformColors(colors.uiBackground, colors.uiBackground)
  CommonInputUI(
    composeNavigator,
    { modifier ->
      EmailInputView(modifier)
    },
    "We will send you an email that will instantly sign you in."
  )
}

@Composable
fun WorkspaceInputUI(composeNavigator: ComposeNavigator) {
  val colors = SlackCloneColorProvider.colors
  PlatformSideEffects.PlatformColors(colors.uiBackground, colors.uiBackground)
  CommonInputUI(
    composeNavigator,
    {
      WorkspaceInputView(it)
    },
    "This is the address you use to sign in to Slack"
  )
}

