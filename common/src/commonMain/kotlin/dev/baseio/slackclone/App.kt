package dev.baseio.slackclone

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import dev.baseio.slackclone.navigation.Navigator
import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackclone.navigation.screen
import dev.baseio.slackclone.uionboarding.compose.EmailAddressInputUI
import dev.baseio.slackclone.uionboarding.compose.GettingStartedUI
import dev.baseio.slackclone.uionboarding.compose.SkipTypingUI
import dev.baseio.slackclone.uionboarding.compose.WorkspaceInputUI

@Composable
fun App(modifier: Modifier = Modifier) {
  Box(modifier){
    Navigator(initialScreen = SlackScreens.GettingStarted) {
      screen(SlackScreens.GettingStarted) {
        GettingStartedUI(this)
      }
      screen(SlackScreens.SkipTypingScreen) {
        SkipTypingUI(this)
      }
      screen(SlackScreens.WorkspaceInputUI) {
        WorkspaceInputUI(this)
      }
      screen(SlackScreens.EmailAddressInputUI) {
        EmailAddressInputUI(this)
      }
    }
  }
}
