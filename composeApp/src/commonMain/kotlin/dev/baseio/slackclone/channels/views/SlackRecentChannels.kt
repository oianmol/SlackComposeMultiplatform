package dev.baseio.slackclone.channels.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.baseio.slackclone.channels.SlackChannelComponent
import dev.baseio.slackclone.channels.SlackChannelVM
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import mainDispatcher

@Composable
internal fun SlackRecentChannels(
    onItemClick: (DomainLayerChannels.SKChannel) -> Unit = {},
    onClickAdd: () -> Unit,
    component: SlackChannelComponent,
    channelVM: SlackChannelVM = component.viewModel
) {
    val recent = "Recent"
    val channels by channelVM.loadRecentChannels().collectAsState(initial = emptyList(), mainDispatcher)

    var expandCollapseModel by remember {
        mutableStateOf(
            ExpandCollapseModel(
                2,
                recent,
                needsPlusButton = false,
                isOpen = true
            )
        )
    }
    SKExpandCollapseColumn(expandCollapseModel, onItemClick, {
        expandCollapseModel = expandCollapseModel.copy(isOpen = it)
    }, channels, onClickAdd)
}
