package dev.baseio.slackclone.uichat.chatthread

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.chatcore.views.SlackChannelItem
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.reusable.SlackListItem
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.uichat.chatthread.composables.ChatScreenContent
import dev.baseio.slackclone.uidashboard.compose.WindowSize
import dev.baseio.slackclone.uidashboard.compose.getWindowSizeClass
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

@Composable
fun ChatScreenUI(
    modifier: Modifier,
    onBackClick: () -> Unit,
    chatScreenComponent: ChatScreenComponent,
    viewModel: ChatViewModel = chatScreenComponent.chatViewModel,
    slackChannel: DomainLayerChannels.SKChannel
) {
    val scaffoldState = rememberScaffoldState()
    val membersDialog by viewModel.showChannelDetails.subscribeAsState()

    LaunchedEffect(slackChannel) {
        viewModel.requestFetch(slackChannel)
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
        Box(Modifier.padding(innerPadding)) {
            ChatScreenContent(
                modifier = Modifier.fillMaxSize(),
                chatScreenComponent
            )
            if (membersDialog) {
                ChannelMembersDialog(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BoxScope.ChannelMembersDialog(viewModel: ChatViewModel) {
    val members by viewModel.channelMembers.subscribeAsState()
    val size = getWindowSizeClass(LocalWindow.current)
    val screenWidth = LocalWindow.current.width
    val width = screenWidth * when (size) {
        WindowSize.Phones -> 1.0f
        WindowSize.SmallTablets -> 0.8f
        WindowSize.BigTablets -> 0.6f
        WindowSize.DesktopOne -> 0.5f
        WindowSize.DesktopTwo -> 0.4f
    }
    val height = LocalWindow.current.height * when (size) {
        WindowSize.Phones -> 1.0f
        WindowSize.SmallTablets -> 0.8f
        WindowSize.BigTablets -> 0.6f
        WindowSize.DesktopOne -> 0.5f
        WindowSize.DesktopTwo -> 0.4f
    }
    Box(
        Modifier.background(color = SlackCloneColorProvider.colors.onUiBackground)
            .fillMaxSize()
    )
    Column(
        modifier = Modifier.align(Alignment.Center).width(width).height(height)
            .background(SlackCloneColorProvider.colors.uiBackground, shape = RoundedCornerShape(12.dp))
    ) {
        ListItem(text = {
            Text("Channel Members")
        }, trailing = {
                IconButton(onClick = {
                    viewModel.showChannelDetailsRequested()
                }) {
                    Icon(Icons.Default.Close, contentDescription = null)
                }
            }, modifier = Modifier.background(SlackCloneColorProvider.colors.appBarColor, shape = RoundedCornerShape(12.dp)))

        LazyColumn(Modifier) {
            items(members) { skUser ->
                SlackListItem(icon = Icons.Default.Person, title = skUser.name ?: "--")
            }
        }
    }
}

@Composable
private fun ChatAppBar(onBackClick: () -> Unit, viewModel: ChatViewModel) {
    val channel by viewModel.channelFlow.subscribeAsState()

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
            horizontalAlignment = Alignment.Start
        ) {
            SlackChannelItem(
                modifier = Modifier,
                slackChannel = channel,
                textColor = SlackCloneColorProvider.colors.appBarTextTitleColor
            ) {
                viewModel.showChannelDetailsRequested()
            }
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

enum class BoxState { Collapsed, Expanded }
