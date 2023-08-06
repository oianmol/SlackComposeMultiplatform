package dev.baseio.slackclone.chatmessaging.newchat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.chatcore.views.SlackChannelItem
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.theme.*
import mainDispatcher

@Composable
internal fun NewChatThreadScreen(
    newChatThread: NewChatThreadComponent
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        backgroundColor = LocalSlackCloneColor.current.uiBackground,
        contentColor = LocalSlackCloneColor.current.textSecondary,
        modifier = Modifier,
        scaffoldState = scaffoldState,
        topBar = {
            SearchAppBar(newChatThread)
        },
        snackbarHost = {
            scaffoldState.snackbarHostState
        }
    ) { innerPadding ->
        SearchContent(innerPadding, newChatThread)
    }
}

@Composable
internal fun SearchContent(
    innerPadding: PaddingValues,
    newChatThread: NewChatThreadComponent
) {
    SlackCloneSurface(
        modifier = Modifier.padding(innerPadding).fillMaxSize()
    ) {
        Column {
            SearchUsersTF(newChatThread)
            ListAllUsers(newChatThread)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ListAllUsers(newChatThreadComponent: NewChatThreadComponent) {
    val channelsFlow by newChatThreadComponent.viewModel.channelsStream.collectAsState(mainDispatcher)
    val errorFlow by newChatThreadComponent.viewModel.errorStream.collectAsState()

    val listState = rememberLazyListState()
    Box {
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
                        newChatThreadComponent.viewModel.createChannel(it)
                    }
                }
                lastDrawnChannel = newDrawn
            }
        }
        errorFlow?.let {
            Text("Unknown Error Occurred! ${it.message}")
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
internal fun SearchUsersTF(newChatThread: NewChatThreadComponent) {
    val searchChannel by newChatThread.viewModel.search.collectAsState(mainDispatcher)

    TextField(
        modifier = Modifier.testTag("searchtf"),
        value = searchChannel,
        onValueChange = { newValue ->
            newChatThread.viewModel.search(newValue)
        },
        textStyle = textStyleFieldPrimary(),
        placeholder = {
            Text(
                text = "Search Channels",
                style = textStyleFieldSecondary(),
                textAlign = TextAlign.Start
            )
        },
        colors = textFieldColors(),
        singleLine = true,
        maxLines = 1
    )
}

@Composable
internal fun textStyleFieldPrimary() = SlackCloneTypography.subtitle1.copy(
    color = LocalSlackCloneColor.current.textPrimary,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Start
)

@Composable
internal fun textStyleFieldSecondary() = SlackCloneTypography.subtitle1.copy(
    color = LocalSlackCloneColor.current.textSecondary,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Start
)

@Composable
internal fun textFieldColors() = TextFieldDefaults.textFieldColors(
    backgroundColor = Color.Transparent,
    cursorColor = LocalSlackCloneColor.current.textPrimary,
    unfocusedIndicatorColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent
)

@Composable
internal fun SearchAppBar(newChatThread: NewChatThreadComponent) {
    SlackSurfaceAppBar(
        title = {
            SearchNavTitle()
        },
        navigationIcon = {
            NavBackIcon(newChatThread)
        },
        backgroundColor = LocalSlackCloneColor.current.appBarColor
    )
}

@Composable
internal fun SearchNavTitle() {
    Text(
        text = "New Message",
        style = SlackCloneTypography.subtitle1.copy(color = LocalSlackCloneColor.current.appBarTextTitleColor)
    )
}

@Composable
internal fun NavBackIcon(newChatThread: NewChatThreadComponent) {
    IconButton(onClick = {
        newChatThread.navigationPop()
    }, modifier = Modifier.testTag("navback")) {
        Icon(
            imageVector = Icons.Filled.Clear,
            contentDescription = "clear",
            modifier = Modifier.padding(start = 8.dp),
            tint = LocalSlackCloneColor.current.appBarIconColor
        )
    }
}
