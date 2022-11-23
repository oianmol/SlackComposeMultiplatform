package dev.baseio.slackclone.uichannels.createsearch

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
import dev.baseio.slackclone.chatcore.views.SlackChannelItem
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.theme.*
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import mainDispatcher

@Composable
internal fun SearchCreateChannelUI(
    searchChannelsComponent: SearchChannelsComponent
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        backgroundColor = LocalSlackCloneColor.current.uiBackground,
        contentColor = LocalSlackCloneColor.current.textSecondary,
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
            searchChannelsComponent.navigationPopWith(skChannel)
        }
    }
}

@Composable
internal fun SearchContent(
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
internal fun ListAllChannels(
    searchChannelsComponent: SearchChannelsComponent,
    onItemClick: (DomainLayerChannels.SKChannel) -> Unit
) {
    val channelsFlow by searchChannelsComponent.viewModel.channels.collectAsState(mainDispatcher)
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
internal fun SlackChannelHeader(title: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(LocalSlackCloneColor.current.lineColor)
    ) {
        Text(
            text = title.toUpperCase(Locale.current),
            modifier = Modifier.padding(12.dp),
            style = SlackCloneTypography.subtitle1.copy(color = LocalSlackCloneColor.current.textSecondary)
        )
    }
}

@Composable
internal fun SearchChannelsTF(searchChannelsComponent: SearchChannelsComponent) {
    val searchChannel by searchChannelsComponent.viewModel.search.collectAsState(mainDispatcher)

    TextField(
        value = searchChannel,
        onValueChange = { newValue ->
            searchChannelsComponent.viewModel.search.value = (newValue)
        },
        textStyle = textStyleFieldPrimaryChannelsScreen(),
        placeholder = {
            Text(
                text = "Search for channels",
                style = textStyleFieldSecondaryChannelsScreen(),
                textAlign = TextAlign.Start
            )
        },
        colors = textFieldColorsChannelsScreen(),
        singleLine = true,
        maxLines = 1
    )
}

@Composable
internal fun textStyleFieldPrimaryChannelsScreen() = SlackCloneTypography.subtitle1.copy(
    color = LocalSlackCloneColor.current.textPrimary,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Start
)

@Composable
internal fun textStyleFieldSecondaryChannelsScreen() = SlackCloneTypography.subtitle1.copy(
    color = LocalSlackCloneColor.current.textSecondary,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Start
)

@Composable
internal fun textFieldColorsChannelsScreen() = TextFieldDefaults.textFieldColors(
    backgroundColor = Color.Transparent,
    cursorColor = LocalSlackCloneColor.current.textPrimary,
    unfocusedIndicatorColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent
)

@Composable
internal fun NewChannelFAB(newChannel: () -> Unit) {
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
internal fun SearchAppBar(searchChannelsComponent: SearchChannelsComponent) {
    val channelCount by searchChannelsComponent.viewModel.channelCount.collectAsState(mainDispatcher)

    SlackSurfaceAppBar(
        title = {
            SearchNavTitle(channelCount)
        },
        navigationIcon = {
            NavBackIcon(searchChannelsComponent)
        },
        backgroundColor = LocalSlackCloneColor.current.appBarColor
    )
}

@Composable
internal fun SearchNavTitle(count: Int) {
    Column {
        Text(
            text = "Channel Browser",
            style = SlackCloneTypography.subtitle1.copy(color = LocalSlackCloneColor.current.appBarTextTitleColor)
        )
        Text(
            text = "$count channels",
            style = SlackCloneTypography.subtitle2.copy(color = LocalSlackCloneColor.current.appBarTextSubTitleColor)
        )
    }
}

@Composable
internal fun NavBackIcon(searchChannelsComponent: SearchChannelsComponent) {
    IconButton(onClick = {
        searchChannelsComponent.navigationPop()
    }) {
        Icon(
            imageVector = Icons.Filled.Clear,
            contentDescription = "Clear",
            modifier = Modifier.padding(start = 8.dp),
            tint = LocalSlackCloneColor.current.appBarIconColor
        )
    }
}
