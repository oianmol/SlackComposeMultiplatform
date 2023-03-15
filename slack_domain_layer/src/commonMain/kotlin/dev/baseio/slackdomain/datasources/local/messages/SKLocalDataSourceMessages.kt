package dev.baseio.slackdomain.datasources.local.messages


import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.coroutines.flow.Flow

interface SKLocalDataSourceMessages {
  fun streamLocalMessages(
    workspaceId: String,
    userId: String,
    limit: Int,
    offset: Int
  ): Flow<List<DomainLayerMessages.SKMessage>>

  fun streamLocalMessages(
    workspaceId: String,
    channelId: String,
  ): Flow<List<DomainLayerMessages.SKMessage>>

  suspend fun getLocalMessages(
    workspaceId: String,
    userId: String,
    limit: Int,
    offset: Int
  ): List<DomainLayerMessages.SKMessage>

  suspend fun getLocalMessages(
    workspaceId: String,
    userId: String,
  ): List<DomainLayerMessages.SKMessage>

  suspend fun saveMessage(
    params: DomainLayerMessages.SKMessage,
  ): DomainLayerMessages.SKMessage
}