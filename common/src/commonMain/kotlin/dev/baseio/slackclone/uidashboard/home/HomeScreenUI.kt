package dev.baseio.slackclone.uidashboard.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.reusable.SlackListItem
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackclone.commonui.reusable.SlackImageBox
import dev.baseio.slackclone.uichannels.SlackChannelComponent
import dev.baseio.slackclone.uichannels.views.*
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces

@Composable
fun HomeScreenUI(
  homeScreenComponent: HomeScreenComponent,
  appBarIconClick: () -> Unit,
  onItemClick: (DomainLayerChannels.SKChannel) -> Unit = {},
  onCreateChannelRequest: () -> Unit = {},
  recentChannelsComponent: SlackChannelComponent,
  allChannelsComponent: SlackChannelComponent
) {
  val selectedWorkspace by homeScreenComponent.lastSelectedWorkspace.value.collectAsState(null)
  SlackCloneSurface(
    color = SlackCloneColorProvider.colors.uiBackground,
    modifier = Modifier.fillMaxSize()
  ) {
    Column() {
      SlackWorkspaceTopAppBar(appBarIconClick, selectedWorkspace)
      Column(Modifier.verticalScroll(rememberScrollState())) {
        JumpToText()
        ThreadsTile()
        SlackListDivider()
        SlackRecentChannels({
          onItemClick(it)
        }, onClickAdd = {
          onCreateChannelRequest()
        }, recentChannelsComponent)
        SlackAllChannels({
          onItemClick(it)
        }, onClickAdd = {
          onCreateChannelRequest()
        }, allChannelsComponent)
      }
    }

  }
}


@Composable
fun ThreadsTile() {
  SlackListItem(icon = Icons.Default.MailOutline, title = "Threads")
}

@Composable
fun JumpToText() {
  Box(
    Modifier
      .fillMaxWidth()
      .clickable { }
      .padding(12.dp)
  ) {
    RoundedCornerBoxDecoration {
      Text(
        text = "Jump to...",
        style = SlackCloneTypography.subtitle2.copy(color = SlackCloneColorProvider.colors.textPrimary),
        modifier = Modifier.fillMaxWidth()
      )
    }
  }

}

@Composable
private fun SlackWorkspaceTopAppBar(
  appBarIconClick: () -> Unit,
  selectedWorkspace: DomainLayerWorkspaces.SKWorkspace?
) {
  SlackSurfaceAppBar(
    title = {
      Text(text = selectedWorkspace?.name ?: "NA", style = SlackCloneTypography.h5.copy(color = Color.White))
    },
    navigationIcon = {
      WorkspaceImageButton(appBarIconClick, selectedWorkspace)
    },
    backgroundColor = SlackCloneColorProvider.colors.appBarColor,
  )
}

@Composable
fun WorkspaceImageButton(appBarIconClick: () -> Unit, selectedWorkspace: DomainLayerWorkspaces.SKWorkspace?) {
  IconButton(onClick = {
    appBarIconClick()
  }) {
    SlackImageBox(
      Modifier.size(38.dp),
      selectedWorkspace?.picUrl ?: "https://avatars.slack-edge.com/2018-07-20/401750958992_1b07bb3c946bc863bfc6_88.png"
    )
  }
}
