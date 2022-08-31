package dev.baseio.slackclone.uidashboard.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.baseio.slackclone.navigation.Navigator
import dev.baseio.slackclone.navigation.SlackComposeNavigator
import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackclone.navigation.screen
import dev.baseio.slackclone.uichannels.createsearch.CreateNewChannelUI
import dev.baseio.slackclone.uichannels.createsearch.SearchCreateChannelUI
import dev.baseio.slackclone.uichat.newchat.NewChatThreadScreen
import dev.baseio.slackclone.uidashboard.compose.DashboardUI

val dashboardNavigator = SlackComposeNavigator(
  SlackScreens.Dashboard
)

@Composable
fun DashboardNavigation(modifier: Modifier = Modifier) {
  Box(modifier) {
    Navigator(dashboardNavigator) {
      screen(SlackScreens.Dashboard) {
        DashboardUI(this)
      }
      screen(SlackScreens.CreateChannelsScreen) {
        SearchCreateChannelUI(composeNavigator = this)
      }
      screen(SlackScreens.CreateNewChannel) {
        CreateNewChannelUI(this)
      }
      screen(SlackScreens.CreateNewDM) {
        NewChatThreadScreen(this)
      }
    }
  }
}