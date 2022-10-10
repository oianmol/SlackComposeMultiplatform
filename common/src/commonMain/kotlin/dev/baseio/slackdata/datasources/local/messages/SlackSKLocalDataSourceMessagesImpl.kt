package dev.baseio.slackdata.datasources.local.messages

import database.SlackMessage
import dev.baseio.database.SlackDB
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdata.local.asFlow
import dev.baseio.slackdata.local.mapToList
import dev.baseio.slackdata.mapper.EntityMapper
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.datasources.local.messages.SKLocalDataSourceMessages
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class SlackSKLocalDataSourceMessagesImpl constructor(
  private val slackMessageDao: SlackDB,
  private val entityMapper: EntityMapper<DomainLayerMessages.SKMessage, SlackMessage>,
  private val coroutineMainDispatcherProvider: CoroutineDispatcherProvider
) : SKLocalDataSourceMessages {
  override fun fetchLocalMessages(workspaceId: String, userId: String): Flow<List<DomainLayerMessages.SKMessage>> {
    return slackMessageDao.slackDBQueries.selectAllMessagesByUserId(workspaceId, userId)
      .asFlow()
      .flowOn(coroutineMainDispatcherProvider.io)
      .mapToList(coroutineMainDispatcherProvider.default)
      .map { slackMessages ->
        slackMessages
          .map { slackMessage -> entityMapper.mapToDomain(slackMessage) }
      }
      .flowOn(coroutineMainDispatcherProvider.default)
  }

  override suspend fun saveMessage(params: DomainLayerMessages.SKMessage): DomainLayerMessages.SKMessage {
    return withContext(coroutineMainDispatcherProvider.io) {
      slackMessageDao.slackDBQueries.insertMessage(
        params.uuid,
        params.workspaceId,
        params.channelId,
        params.message,
        params.receiver,
        params.sender,
        params.createdDate,
        params.modifiedDate,
      )
      params
    }
  }
}