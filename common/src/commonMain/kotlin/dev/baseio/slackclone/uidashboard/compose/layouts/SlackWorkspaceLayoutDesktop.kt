package dev.baseio.slackclone.uidashboard.compose.layouts

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.slackComponent
import dev.baseio.slackclone.uichannels.views.*
import dev.baseio.slackclone.uidashboard.compose.WorkspacesBar
import dev.baseio.slackclone.uidashboard.compose.FloatingDM
import dev.baseio.slackclone.uidashboard.home.JumpToText
import dev.baseio.slackclone.uidashboard.home.ThreadsTile

@Composable
fun SlackWorkspaceLayoutDesktop(
  modifier: Modifier = Modifier,
  onItemClick: (UiLayerChannels.SlackChannel) -> Unit,
  onCreateChannelRequest: () -> Unit = {},
  composeNavigator: ComposeNavigator
) {
  Scaffold(floatingActionButton = {
    FloatingDM(composeNavigator, onItemClick)
  }, modifier = modifier, backgroundColor = SlackCloneColorProvider.colors.uiBackground) {
    SlackCloneSurface(
      color = SlackCloneColorProvider.colors.uiBackground,
      modifier = Modifier.padding(it)
    ) {
      Column {
        WorkspacesBar()
        JumpToText()
        Spacer(modifier = Modifier.height(8.dp))
        ThreadsTile()
        Divider(color = SlackCloneColorProvider.colors.lineColor, thickness = 0.5.dp)
        SlackRecentChannels({
          onItemClick(it)
        }, onClickAdd = {
          onCreateChannelRequest()
        })
        SlackStarredChannels({
          onItemClick(it)
        }, onClickAdd = {
          onCreateChannelRequest()
        })
        SlackDirectMessages({
          onItemClick(it)
        }, onClickAdd = {
          onCreateChannelRequest()
        })
        SlackAllChannels({
          onItemClick(it)
        }, onClickAdd = {
          onCreateChannelRequest()
        }, channelVM = slackComponent.provideSlackChannelVM())
        SlackConnections({
          onItemClick(it)
        }, onClickAdd = {
          onCreateChannelRequest()
        }, channelVM = slackComponent.provideSlackChannelVM())
      }
    }
  }

}