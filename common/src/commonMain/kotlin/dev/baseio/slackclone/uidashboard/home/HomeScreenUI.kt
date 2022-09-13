package dev.baseio.slackclone.uidashboard.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.reusable.SlackListItem
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackclone.commonui.reusable.SlackImageBox
import dev.baseio.slackclone.slackComponent
import dev.baseio.slackclone.uichannels.views.*

@Composable
fun HomeScreenUI(
  appBarIconClick: () -> Unit,
  onItemClick: (UiLayerChannels.SKChannel) -> Unit = {},
  onCreateChannelRequest: () -> Unit = {}
) {
  SlackCloneSurface(
    color = SlackCloneColorProvider.colors.uiBackground,
    modifier = Modifier.fillMaxSize()
  ) {
    Column() {
      SlackWorkspaceTopAppBar(appBarIconClick)
      Column(Modifier.verticalScroll(rememberScrollState())) {
        JumpToText()
        ThreadsTile()
        SlackListDivider()
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
        }, slackComponent.provideSlackChannelVM())
        SlackConnections({
          onItemClick(it)
        }, onClickAdd = {
          onCreateChannelRequest()
        }, slackComponent.provideSlackChannelVM())
      }
    }

  }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ThreadsTile() {
  SlackListItem(Icons.Default.MailOutline, "Threads")
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
private fun SlackWorkspaceTopAppBar(appBarIconClick: () -> Unit) {
  SlackSurfaceAppBar(
    title = {
      Text(text = "mutualmobile", style = SlackCloneTypography.h5.copy(color = Color.White))
    },
    navigationIcon = {
      MMImageButton(appBarIconClick)
    },
    backgroundColor = SlackCloneColorProvider.colors.appBarColor,
  )
}

@Composable
fun MMImageButton(appBarIconClick: () -> Unit) {
  IconButton(onClick = {
    appBarIconClick()
  }) {
    SlackImageBox(
      Modifier.size(38.dp),
      "https://avatars.slack-edge.com/2018-07-20/401750958992_1b07bb3c946bc863bfc6_88.png"
    )
  }
}
