package dev.baseio.slackclone.uichat.chatthread

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackclone.chatcore.views.SlackChannelItem
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.uichat.chatthread.composables.ChatScreenContent

@Composable
fun ChatScreenUI(
  modifier: Modifier,
  SKChannel: DomainLayerChannels.SKChannel,
  onBackClick: () -> Unit,
  viewModel: ChatScreenVM
) {
  val scaffoldState = rememberScaffoldState()
  LaunchedEffect(SKChannel) {
    viewModel.requestFetch(SKChannel)
  }

  Scaffold(
    backgroundColor = SlackCloneColorProvider.colors.uiBackground,
    contentColor = SlackCloneColorProvider.colors.textSecondary,
    modifier = modifier,
    scaffoldState = scaffoldState,
    snackbarHost = {
      scaffoldState.snackbarHostState
    },
    topBar = {
      ChatAppBar(onBackClick, viewModel)
    }
  ) { innerPadding ->
    ChatScreenContent(
      modifier = Modifier
        .padding(innerPadding), viewModel
    )
  }

}


@Composable
private fun ChatAppBar(onBackClick: () -> Unit,viewModel: ChatScreenVM) {
  val channel by viewModel.channelFlow.collectAsState()

  SlackSurfaceAppBar(backgroundColor = SlackCloneColorProvider.colors.appBarColor) {
    IconButton(onClick = { onBackClick() }) {
      Icon(
        imageVector = Icons.Default.ArrowBack,
        contentDescription = null,
        tint = SlackCloneColorProvider.colors.appBarIconColor,
        modifier = Modifier.size(24.dp)
      )
    }
    Column(
      Modifier.weight(1f),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      SlackChannelItem(
        slackChannel = channel,
        textColor = SlackCloneColorProvider.colors.appBarTextTitleColor
      ) {}
    }
    IconButton(onClick = { }) {
      Icon(
        imageVector = Icons.Default.Call,
        contentDescription = null,
        tint = SlackCloneColorProvider.colors.appBarIconColor,
        modifier = Modifier
          .size(24.dp)
      )
    }
  }
}

fun lock() = "\uD83D\uDD12"

enum class BoxState { Collapsed, Expanded }