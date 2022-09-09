package dev.baseio.slackclone.uidashboard.compose.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.uidashboard.home.SlackListDivider

@Composable
fun SlackDesktopLayout(
  modifier: Modifier = Modifier,
  sideBar: @Composable (Modifier) -> Unit,
  workSpaceAndChannels: @Composable (Modifier) -> Unit,
  content: @Composable (Modifier) -> Unit
) {
  Row(modifier) {
    sideBar(Modifier.width(80.dp).fillMaxHeight())
    workSpaceAndChannels(Modifier.weight(1f).fillMaxHeight())
    content(Modifier.weight(3f).fillMaxHeight())
  }

}