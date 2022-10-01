package dev.baseio.slackdomain.datasources.local.messages


import dev.baseio.slackdomain.model.message.DomainLayerMessages
import kotlinx.coroutines.flow.Flow

interface SKDataSourceMessages {
  fun fetchMessages(workspaceId: String, userId: String): Flow<List<DomainLayerMessages.SKMessage>>
  suspend fun sendMessage(params: DomainLayerMessages.SKMessage): DomainLayerMessages.SKMessage
}