package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceChannelLastMessage
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.BaseUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class UseCaseFetchRecentChannels(private val skLocalDataSourceChannelLastMessage: SKLocalDataSourceChannelLastMessage) {
    operator fun invoke(workspaceId: String): Flow<List<DomainLayerChannels.SKChannel>> {
        return skLocalDataSourceChannelLastMessage.fetchChannelsWithLastMessage(workspaceId = workspaceId)
            .mapLatest { skLastMessageList ->
                skLastMessageList.map { skLastMessage -> skLastMessage.channel }
            }
    }
}