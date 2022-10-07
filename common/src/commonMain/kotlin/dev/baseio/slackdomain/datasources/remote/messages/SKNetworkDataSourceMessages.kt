package dev.baseio.slackdomain.datasources.remote.messages

import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import kotlinx.coroutines.flow.Flow

interface SKNetworkDataSourceMessages {
  suspend fun sendMessage(params: DomainLayerMessages.SKMessage): DomainLayerMessages.SKMessage
  fun getMessages(request: UseCaseChannelRequest): Flow<List<DomainLayerMessages.SKMessage>>
}