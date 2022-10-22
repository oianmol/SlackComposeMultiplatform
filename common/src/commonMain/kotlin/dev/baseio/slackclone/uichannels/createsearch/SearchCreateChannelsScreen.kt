package dev.baseio.slackclone.uichannels.createsearch

import mainDispatcher
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.theme.*
import dev.baseio.slackclone.chatcore.views.SlackChannelItem

@Composable
fun SearchCreateChannelUI(
  searchChannelsComponent: SearchChannelsComponent,
) {

  val scaffoldState = rememberScaffoldState()

  Scaffold(
    backgroundColor = SlackCloneColorProvider.colors.uiBackground,
    contentColor = SlackCloneColorProvider.colors.textSecondary,
    modifier = Modifier,
    scaffoldState = scaffoldState,
    topBar = {
      SearchAppBar(searchChannelsComponent)
    },
    snackbarHost = {
      scaffoldState.snackbarHostState
    },
    floatingActionButton = {
      NewChannelFAB {
        searchChannelsComponent.navigateRoot(RootComponent.Config.CreateNewChannelUI)
      }
    }
  ) { innerPadding ->
    SearchContent(innerPadding, searchChannelsComponent) { skChannel ->
      //searchChannelsComponent.navigateChannel(skChannel)
    }
  }
}

@Composable
private fun SearchContent(
  innerPadding: PaddingValues,
  searchChannelsComponent: SearchChannelsComponent,
  onItemClick: (DomainLayerChannels.SKChannel) -> Unit
) {
  Box(modifier = Modifier.padding(innerPadding)) {
    SlackCloneSurface(
      modifier = Modifier.fillMaxSize()
    ) {
      Column {
        SearchChannelsTF(searchChannelsComponent)
        ListAllChannels(searchChannelsComponent, onItemClick)
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListAllChannels(
  searchChannelsComponent: SearchChannelsComponent,
  onItemClick: (DomainLayerChannels.SKChannel) -> Unit
) {
  val channelsFlow by searchChannelsComponent.channels.collectAsState(mainDispatcher)
  val listState = rememberLazyListState()
  LazyColumn(state = listState, reverseLayout = false) {
    var lastDrawnChannel: String? = null
    for (channelIndex in channelsFlow.indices) {
      val channel = channelsFlow[channelIndex]
      val newDrawn = channel.channelName?.firstOrNull().toString()
      if (canDrawHeader(lastDrawnChannel, newDrawn)) {
        stickyHeader {
          SlackChannelHeader(newDrawn)
        }
      }
      item {
        SlackChannelItem(slackChannel = channel) {
          onItemClick(it)
        }
      }
      lastDrawnChannel = newDrawn
    }
  }
}

fun canDrawHeader(lastDrawnChannel: String?, name: String?): Boolean {
  return lastDrawnChannel != name
}

@Composable
fun SlackChannelHeader(title: String) {
  Box(
    Modifier
      .fillMaxWidth()
      .background(SlackCloneColorProvider.colors.lineColor)
  ) {
    Text(
      text = title.toUpperCase(Locale.current),
      modifier = Modifier.padding(12.dp),
      style = SlackCloneTypography.subtitle1.copy(color = SlackCloneColorProvider.colors.textSecondary)
    )
  }
}

@Composable
private fun SearchChannelsTF(searchChannelsComponent: SearchChannelsComponent) {
  val searchChannel by searchChannelsComponent.search.collectAsState(mainDispatcher)

  TextField(
    value = searchChannel,
    onValueChange = { newValue ->
      searchChannelsComponent.search.value = (newValue)
    },
    textStyle = textStyleFieldPrimary(),
    placeholder = {
      Text(
        text = "Search for channels",
        style = textStyleFieldSecondary(),
        textAlign = TextAlign.Start
      )
    },
    colors = textFieldColors(),
    singleLine = true,
    maxLines = 1,
  )
}

@Composable
private fun textStyleFieldPrimary() = SlackCloneTypography.subtitle1.copy(
  color = SlackCloneColorProvider.colors.textPrimary,
  fontWeight = FontWeight.Normal,
  textAlign = TextAlign.Start
)


@Composable
private fun textStyleFieldSecondary() = SlackCloneTypography.subtitle1.copy(
  color = SlackCloneColorProvider.colors.textSecondary,
  fontWeight = FontWeight.Normal,
  textAlign = TextAlign.Start
)

@Composable
private fun textFieldColors() = TextFieldDefaults.textFieldColors(
  backgroundColor = Color.Transparent,
  cursorColor = SlackCloneColorProvider.colors.textPrimary,
  unfocusedIndicatorColor = Color.Transparent,
  focusedIndicatorColor = Color.Transparent
)

@Composable
private fun NewChannelFAB(newChannel: () -> Unit) {
  FloatingActionButton(onClick = {
    newChannel()
  }, backgroundColor = Color.White) {
    Icon(
      imageVector = Icons.Default.Add,
      contentDescription = null,
      tint = SlackCloneColor
    )
  }
}

@Composable
private fun SearchAppBar(searchChannelsComponent: SearchChannelsComponent) {
  val channelCount by searchChannelsComponent.channelCount.collectAsState(mainDispatcher)

  SlackSurfaceAppBar(
    title = {
      SearchNavTitle(channelCount)
    },
    navigationIcon = {
      NavBackIcon(searchChannelsComponent)
    },
    backgroundColor = SlackCloneColorProvider.colors.appBarColor,
  )
}

@Composable
private fun SearchNavTitle(count: Int) {
  Column {
    Text(
      text = "Channel Browser",
      style = SlackCloneTypography.subtitle1.copy(color = SlackCloneColorProvider.colors.appBarTextTitleColor)
    )
    Text(
      text = "$count channels",
      style = SlackCloneTypography.subtitle2.copy(color = SlackCloneColorProvider.colors.appBarTextSubTitleColor)
    )
  }
}

@Composable
private fun NavBackIcon(searchChannelsComponent: SearchChannelsComponent) {
  IconButton(onClick = {
    searchChannelsComponent.navigationPop()
  }) {
    Icon(
      imageVector = Icons.Filled.Clear,
      contentDescription = "Clear",
      modifier = Modifier.padding(start = 8.dp),
      tint = SlackCloneColorProvider.colors.appBarIconColor
    )
  }
}
