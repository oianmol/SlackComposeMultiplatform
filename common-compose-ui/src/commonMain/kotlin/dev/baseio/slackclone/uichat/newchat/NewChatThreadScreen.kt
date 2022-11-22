package dev.baseio.slackclone.uichat.newchat

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
fun NewChatThreadScreen(
    newChatThread: NewChatThreadComponent
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        backgroundColor = SlackCloneColorProvider.colors.uiBackground,
        contentColor = SlackCloneColorProvider.colors.textSecondary,
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
private fun SearchContent(
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
private fun ListAllUsers(newChatThreadComponent: NewChatThreadComponent) {
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
private fun SearchUsersTF(newChatThread: NewChatThreadComponent) {
    val searchChannel by newChatThread.viewModel.search.collectAsState(mainDispatcher)

    TextField(
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
private fun SearchAppBar(newChatThread: NewChatThreadComponent) {
    SlackSurfaceAppBar(
        title = {
            SearchNavTitle()
        },
        navigationIcon = {
            NavBackIcon(newChatThread)
        },
        backgroundColor = SlackCloneColorProvider.colors.appBarColor
    )
}

@Composable
private fun SearchNavTitle() {
    Text(
        text = "New Message",
        style = SlackCloneTypography.subtitle1.copy(color = SlackCloneColorProvider.colors.appBarTextTitleColor)
    )
}

@Composable
private fun NavBackIcon(newChatThread: NewChatThreadComponent) {
    IconButton(onClick = {
        newChatThread.navigationPop()
    }) {
        Icon(
            imageVector = Icons.Filled.Clear,
            contentDescription = "clear",
            modifier = Modifier.padding(start = 8.dp),
            tint = SlackCloneColorProvider.colors.appBarIconColor
        )
    }
}
