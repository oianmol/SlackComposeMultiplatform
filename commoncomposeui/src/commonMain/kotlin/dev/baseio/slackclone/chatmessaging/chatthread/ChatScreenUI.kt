package dev.baseio.slackclone.chatmessaging.chatthread

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import dev.baseio.slackclone.chatmessaging.chatthread.composables.ChatScreenContent
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ChatScreenUI(
    modifier: Modifier,
    onBackClick: () -> Unit,
    chatScreenComponent: ChatScreenComponent,
    viewModel: ChatViewModel = chatScreenComponent.chatViewModel,
    slackChannel: DomainLayerChannels.SKChannel
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    var membersDialog by remember { mutableStateOf(false) }
    val offerSecurityKeys by viewModel.securityKeyOffer.collectAsState()
    val requestSecurityKeys by viewModel.securityKeyRequested.collectAsState()
    val members by viewModel.channelMembers.collectAsState()
    val channel by viewModel.channelFlow.subscribeAsState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(slackChannel) {
        viewModel.requestFetch(slackChannel)
    }

    BottomSheetScaffold(
        backgroundColor = LocalSlackCloneColor.current.uiBackground,
        contentColor = LocalSlackCloneColor.current.textSecondary,
        sheetBackgroundColor = LocalSlackCloneColor.current.uiBackground,
        sheetContentColor = LocalSlackCloneColor.current.textSecondary,
        sheetPeekHeight = 0.dp,
        modifier = modifier,
        scaffoldState = scaffoldState,
        snackbarHost = {
            scaffoldState.snackbarHostState
        },
        topBar = {
            ChatAppBar(onBackClick, channel) {
                membersDialog = true
                scope.launch {
                    scaffoldState.bottomSheetState.expand()
                }
            }
        },
        sheetContent = {
            if (membersDialog) {
                ChannelMembersDialog(members) {
                    membersDialog = false
                    scope.launch {
                        scaffoldState.bottomSheetState.collapse()
                    }
                }
            }
            if (offerSecurityKeys) {
                SecurityKeysOfferUI()
            }
            if (requestSecurityKeys) {
                SecurityKeysRequestUI()
            }
        },
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            ChatScreenContent(
                modifier = Modifier.fillMaxSize(),
                chatScreenComponent
            )
        }
    }
}

