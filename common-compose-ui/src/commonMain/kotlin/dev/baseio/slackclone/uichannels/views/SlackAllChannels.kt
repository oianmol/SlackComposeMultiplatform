package dev.baseio.slackclone.uichannels.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.baseio.slackclone.uichannels.SlackChannelComponent
import dev.baseio.slackclone.uichannels.SlackChannelVM
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import mainDispatcher

@Composable
internal fun SlackAllChannels(
    onItemClick: (DomainLayerChannels.SKChannel) -> Unit = {},
    onClickAdd: () -> Unit,
    component: SlackChannelComponent,
    channelVM: SlackChannelVM = component.viewModel
) {
    val recent = "Channels"
    val channels by channelVM.allChannels().collectAsState(emptyList(), mainDispatcher)

    var expandCollapseModel by remember {
        mutableStateOf(
            ExpandCollapseModel(
                1,
                recent,
                needsPlusButton = true,
                isOpen = false
            )
        )
    }
    SKExpandCollapseColumn(expandCollapseModel = expandCollapseModel, onItemClick = onItemClick, {
        expandCollapseModel = expandCollapseModel.copy(isOpen = it)
    }, channels, onClickAdd)
}
