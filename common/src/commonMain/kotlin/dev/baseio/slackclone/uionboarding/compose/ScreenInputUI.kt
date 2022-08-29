package dev.baseio.slackclone.uionboarding.compose

import androidx.compose.runtime.*
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import dev.baseio.slackclone.navigation.ComposeNavigator

@Composable
fun EmailAddressInputUI(composeNavigator: ComposeNavigator) {
  CommonInputUI(
    composeNavigator,
    { modifier ->
      EmailInputView(modifier)
    },
    "We\\'ll send you an email that\\'ll instantly sign you in."
  )
}

@Composable
fun WorkspaceInputUI(composeNavigator: ComposeNavigator) {
  CommonInputUI(
    composeNavigator,
    {
      WorkspaceInputView(it)
    },
    "This is the address you use to sign in to Slack"
  )
}

