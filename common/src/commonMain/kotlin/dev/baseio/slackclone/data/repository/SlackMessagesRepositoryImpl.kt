package dev.baseio.slackclone.data.repository

import database.SlackMessage
import dev.baseio.database.SlackDB
import dev.baseio.slackclone.common.injection.dispatcher.CoroutineDispatcherProvider
import dev.baseio.slackclone.data.local.asFlow
import dev.baseio.slackclone.data.local.mapToList
import dev.baseio.slackclone.data.mapper.EntityMapper
import dev.baseio.slackclone.domain.model.message.DomainLayerMessages
import dev.baseio.slackclone.domain.repository.MessagesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class SlackMessagesRepositoryImpl constructor(
  private val slackMessageDao: SlackDB,
  private val entityMapper: EntityMapper<DomainLayerMessages.SlackMessage, SlackMessage>,
  private val coroutineMainDispatcherProvider: CoroutineDispatcherProvider
) : MessagesRepository {
  override fun fetchMessages(params: String?): Flow<List<DomainLayerMessages.SlackMessage>> {
    val list = slackMessageDao.slackDBQueries.selectAllMessagesByUserId(params!!).asFlow().mapToList(coroutineMainDispatcherProvider.default)
    return list.map { it -> it.map { entityMapper.mapToDomain(it) } }
  }

  override suspend fun sendMessage(params: DomainLayerMessages.SlackMessage): DomainLayerMessages.SlackMessage {
    return withContext(coroutineMainDispatcherProvider.io) {
      slackMessageDao.slackDBQueries.insertMessage(
        params.uuid,
        params.channelId,
        params.message,
        params.userId,
        params.createdBy,
        params.createdDate,
        params.modifiedDate
      )
      params
    }
  }
}