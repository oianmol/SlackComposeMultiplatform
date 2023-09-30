package dev.baseio.slackclone.chatmessaging.chatthread

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import dev.baseio.slackclone.chatmessaging.chatthread.composables.ChatScreenContent
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ChatScreenUI(
    modifier: Modifier,
    onBackClick: () -> Unit,
    chatScreenComponent: ChatScreenComponent,
    viewModel: ChatViewModel = chatScreenComponent.chatViewModel,
    slackChannel: DomainLayerChannels.SKChannel
) {
    val scaffoldState = rememberScaffoldState()
    var membersDialog by remember { mutableStateOf(false) }
    val offerSecurityKeys by viewModel.securityKeyOffer.collectAsState()
    val requestSecurityKeys by viewModel.securityKeyRequested.collectAsState()
    val members by viewModel.channelMembers.collectAsState()
    val channel by viewModel.channelFlow.subscribeAsState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(slackChannel) {
        viewModel.requestFetch(slackChannel)
    }

    Scaffold(
        backgroundColor = LocalSlackCloneColor.current.uiBackground,
        contentColor = LocalSlackCloneColor.current.textSecondary,
        modifier = modifier,
        scaffoldState = scaffoldState,
        snackbarHost = {
            scaffoldState.snackbarHostState
        },
        topBar = {
            ChatAppBar(onBackClick, channel) {
                membersDialog = true
            }
        },
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            ChatScreenContent(
                modifier = Modifier.fillMaxSize(),
                chatScreenComponent
            )

            if (membersDialog) {
                ChannelMembersDialog(members) {
                    membersDialog = false
                }
            }
            if (offerSecurityKeys) {
                SecurityKeysOfferUI(viewModel)
            }
            if (requestSecurityKeys) {
                SecurityKeysRequestUI(viewModel)
            }
        }
    }
}

