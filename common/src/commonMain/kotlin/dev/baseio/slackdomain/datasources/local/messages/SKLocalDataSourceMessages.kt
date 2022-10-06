package dev.baseio.slackdomain.datasources.local.messages


import dev.baseio.slackdomain.model.message.DomainLayerMessages
import kotlinx.coroutines.flow.Flow

interface SKLocalDataSourceMessages {
  fun fetchLocalMessages(workspaceId: String, userId: String): Flow<List<DomainLayerMessages.SKMessage>>
  suspend fun saveMessage(params: DomainLayerMessages.SKMessage): DomainLayerMessages.SKMessage
}