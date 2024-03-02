package dev.baseio.slackdomain.datasources.remote.messages

import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import kotlinx.coroutines.flow.Flow

interface SKNetworkDataSourceMessages {
    suspend fun sendMessage(params: DomainLayerMessages.SKMessage, publicKey: DomainLayerUsers.SKSlackKey): DomainLayerMessages.SKMessage
    suspend fun fetchMessages(request: UseCaseWorkspaceChannelRequest): Result<List<DomainLayerMessages.SKMessage>>
    fun registerChangeInMessages(request: UseCaseWorkspaceChannelRequest): Flow<Pair<DomainLayerMessages.SKMessage?, DomainLayerMessages.SKMessage?>>
    suspend fun deleteMessage(
        params: DomainLayerMessages.SKMessage,
        publicKey: DomainLayerUsers.SKSlackKey
    ): DomainLayerMessages.SKMessage
}
