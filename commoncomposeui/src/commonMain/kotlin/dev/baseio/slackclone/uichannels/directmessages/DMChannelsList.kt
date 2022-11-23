package dev.baseio.slackclone.uichannels.directmessages

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.baseio.slackclone.chatcore.views.DMLastMessageItem
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import mainDispatcher

@Composable
internal fun DMChannelsList(
    onItemClick: (DomainLayerChannels.SKChannel) -> Unit,
    component: DirectMessagesComponent
) {
    val channels by component.viewModel.channels.collectAsState(mainDispatcher)
    val channelsFlow by channels.collectAsState(emptyList(), mainDispatcher)
    val listState = rememberLazyListState()

    LazyColumn(state = listState) {
        for (channelIndex in channelsFlow.indices) {
            val channel = channelsFlow[channelIndex]

            item {
                DMLastMessageItem({
                    onItemClick(it)
                }, channel.channel, channel.message)
            }
        }
    }
}
