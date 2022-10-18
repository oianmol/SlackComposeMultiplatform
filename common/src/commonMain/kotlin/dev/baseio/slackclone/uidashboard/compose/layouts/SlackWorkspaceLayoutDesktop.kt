package dev.baseio.slackclone.uidashboard.compose.layouts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.data.injection.AllChatsQualifier
import dev.baseio.slackclone.data.injection.RecentChatsQualifier
import dev.baseio.slackclone.navigation.BackstackScreen
import dev.baseio.slackclone.navigation.ComposeNavigator

import dev.baseio.slackclone.uichannels.views.*
import dev.baseio.slackclone.uidashboard.compose.WorkspacesBar
import dev.baseio.slackclone.uidashboard.compose.FloatingDM
import dev.baseio.slackclone.uidashboard.home.JumpToText
import dev.baseio.slackclone.uidashboard.home.ThreadsTile

@Composable
fun BackstackScreen.SlackWorkspaceLayoutDesktop(
  modifier: Modifier = Modifier,
  onItemClick: (DomainLayerChannels.SKChannel) -> Unit,
  onCreateChannelRequest: () -> Unit = {},
  composeNavigator: ComposeNavigator
) {
  Scaffold(
    backgroundColor = SlackCloneColorProvider.colors.uiBackground,
    contentColor = SlackCloneColorProvider.colors.textSecondary,
    floatingActionButton = {
      FloatingDM(composeNavigator, onItemClick)
    }, modifier = modifier
  ) {
    SlackCloneSurface(
      color = SlackCloneColorProvider.colors.uiBackground,
      modifier = Modifier.padding(it)
    ) {
      Column(Modifier.verticalScroll(rememberScrollState())) {
        WorkspacesBar()
        JumpToText()
        Spacer(modifier = Modifier.height(8.dp))
        ThreadsTile()
        Divider(color = SlackCloneColorProvider.colors.lineColor, thickness = 0.5.dp)
        SlackRecentChannels({
          onItemClick(it)
        }, onClickAdd = {
          onCreateChannelRequest()
        },scope.get(RecentChatsQualifier))
        SlackAllChannels({
          onItemClick(it)
        }, onClickAdd = {
          onCreateChannelRequest()
        }, scope.get(AllChatsQualifier))
      }
    }
  }

}