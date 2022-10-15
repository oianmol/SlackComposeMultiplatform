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
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.theme.*
import dev.baseio.slackclone.chatcore.views.SlackChannelItem
import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.navigation.NavigationKey
import dev.baseio.slackclone.navigation.SlackScreens

@Composable
fun SearchCreateChannelUI(
  composeNavigator: ComposeNavigator,
  searchChannelsVM: SearchChannelsVM,
) {

  val scaffoldState = rememberScaffoldState()

  ListChannels(scaffoldState, composeNavigator, searchChannelsVM = searchChannelsVM, { slackChannel: Any ->
    val channel = slackChannel as UiLayerChannels.SKChannel
    composeNavigator.navigateUp()
    composeNavigator.deliverResult(NavigationKey.NavigateChannel, channel, SlackScreens.Dashboard)
  }) {
    composeNavigator.registerForNavigationResult(
      NavigationKey.NavigateChannel,
      SlackScreens.CreateChannelsScreen
    ) { slackChannel: Any ->
      val channel = slackChannel as UiLayerChannels.SKChannel
      composeNavigator.navigateUp()
      composeNavigator.deliverResult(NavigationKey.NavigateChannel, channel, SlackScreens.Dashboard)
    }
    composeNavigator.navigateScreen(SlackScreens.CreateNewChannel)
  }
}

@Composable
private fun ListChannels(
  scaffoldState: ScaffoldState,
  composeNavigator: ComposeNavigator,
  searchChannelsVM: SearchChannelsVM,
  onItemClick: (Any) -> Unit,
  newChannel: () -> Unit
) {
  Scaffold(
    backgroundColor = SlackCloneColorProvider.colors.uiBackground,
    contentColor = SlackCloneColorProvider.colors.textSecondary,
    modifier = Modifier,
    scaffoldState = scaffoldState,
    topBar = {
      val channelCount by searchChannelsVM.channelCount.collectAsState(mainDispatcher)
      SearchAppBar(composeNavigator, channelCount)
    },
    snackbarHost = {
      scaffoldState.snackbarHostState
    },
    floatingActionButton = {
      NewChannelFAB(newChannel)
    }
  ) { innerPadding ->
    SearchContent(innerPadding, searchChannelsVM, onItemClick)
  }
}

@Composable
private fun SearchContent(
  innerPadding: PaddingValues,
  searchChannelsVM: SearchChannelsVM,
  onItemClick: (UiLayerChannels.SKChannel) -> Unit
) {
  Box(modifier = Modifier.padding(innerPadding)) {
    SlackCloneSurface(
      modifier = Modifier.fillMaxSize()
    ) {
      Column {
        SearchChannelsTF(searchChannelsVM)
        ListAllChannels(searchChannelsVM, onItemClick)
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListAllChannels(
  searchChannelsVM: SearchChannelsVM,
  onItemClick: (UiLayerChannels.SKChannel) -> Unit
) {
  val channels by searchChannelsVM.channels.collectAsState(mainDispatcher)
  val channelsFlow by channels.collectAsState(emptyList(), mainDispatcher)
  val listState = rememberLazyListState()
  LazyColumn(state = listState, reverseLayout = false) {
    var lastDrawnChannel: String? = null
    for (channelIndex in channelsFlow.indices) {
      val channel = channelsFlow[channelIndex]
      val newDrawn = channel.name?.firstOrNull().toString()
      if (canDrawHeader(lastDrawnChannel, newDrawn)) {
        stickyHeader {
          SlackChannelHeader(newDrawn)
        }
      }
      item {
        SlackChannelItem(channel) {
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SlackChannelListItem(SKChannel: UiLayerChannels.SKChannel) {
  Column {
    SlackChannelItem(SKChannel) {

    }
    Divider(color = SlackCloneColorProvider.colors.lineColor, thickness = 0.5.dp)
  }
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
private fun SearchChannelsTF(searchChannelsVM: SearchChannelsVM) {
  val searchChannel by searchChannelsVM.search.collectAsState(mainDispatcher)

  TextField(
    value = searchChannel,
    onValueChange = { newValue ->
      searchChannelsVM.search(newValue)
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
private fun SearchAppBar(composeNavigator: ComposeNavigator, count: Int) {
  SlackSurfaceAppBar(
    title = {
      SearchNavTitle(count)
    },
    navigationIcon = {
      NavBackIcon(composeNavigator)
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
private fun NavBackIcon(composeNavigator: ComposeNavigator) {
  IconButton(onClick = {
    composeNavigator.navigateUp()
  }) {
    Icon(
      imageVector = Icons.Filled.Clear,
      contentDescription = "Clear",
      modifier = Modifier.padding(start = 8.dp),
      tint = SlackCloneColorProvider.colors.appBarIconColor
    )
  }
}
