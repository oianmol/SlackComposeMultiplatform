package dev.baseio.slackdomain.usecases.chat

import dev.baseio.slackdomain.datasources.local.messages.SKLocalDataSourceMessages
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import kotlinx.coroutines.flow.Flow

class UseCaseStreamLocalMessages(
    private val skLocalDataSourceMessages: SKLocalDataSourceMessages
) {
    operator fun invoke(useCaseWorkspaceChannelRequest: UseCaseWorkspaceChannelRequest): Flow<List<DomainLayerMessages.SKMessage>> {
        return skLocalDataSourceMessages.streamLocalMessages(
            workspaceId = useCaseWorkspaceChannelRequest.workspaceId,
            useCaseWorkspaceChannelRequest.channelId!!,
        )
    }
}
