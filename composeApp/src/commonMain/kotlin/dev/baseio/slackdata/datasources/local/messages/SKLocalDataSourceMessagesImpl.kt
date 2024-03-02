package dev.baseio.slackdata.datasources.local.messages

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import database.SlackMessage
import dev.baseio.database.SlackDB
import dev.baseio.slackdata.mapper.EntityMapper
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.local.messages.IMessageDecrypter
import dev.baseio.slackdomain.datasources.local.messages.SKLocalDataSourceMessages
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SKLocalDataSourceMessagesImpl(
    private val slackMessageDao: SlackDB,
    private val entityMapper: EntityMapper<DomainLayerMessages.SKMessage, SlackMessage>,
    private val coroutineMainDispatcherProvider: CoroutineDispatcherProvider,
    private val iMessageDecrypter: IMessageDecrypter
) : SKLocalDataSourceMessages {

    override suspend fun getLocalMessages(
        workspaceId: String,
        userId: String
    ): List<DomainLayerMessages.SKMessage> {
        return slackMessageDao.slackDBQueries.selectAllMessagesByChannelId(
            workspaceId,
            userId,
        ).executeAsList().run {
            this
                .map { slackMessage -> entityMapper.mapToDomain(slackMessage) }
        }
    }

    override fun streamLocalMessages(
        workspaceId: String,
        channelId: String
    ): Flow<List<DomainLayerMessages.SKMessage>> {
        return slackMessageDao.slackDBQueries.selectAllMessagesByChannelId(
            workspaceId,
            channelId,
        )
            .asFlow()
            .flowOn(coroutineMainDispatcherProvider.io)
            .mapToList(coroutineMainDispatcherProvider.default)
            .map { slackMessages ->
                slackMessages.map { slackMessage ->
                    val message = entityMapper.mapToDomain(slackMessage)
                    iMessageDecrypter.decrypted(message).getOrNull() ?: message
                }
            }
            .flowOn(coroutineMainDispatcherProvider.default)
    }

    override suspend fun getLocalMessages(
        workspaceId: String,
        userId: String,
        limit: Int,
        offset: Int
    ): List<DomainLayerMessages.SKMessage> {
        return slackMessageDao.slackDBQueries.selectAllMessagesByChannelIdPaginated(
            workspaceId,
            userId,
            limit.toLong(),
            offset.toLong()
        ).executeAsList().run {
            this
                .map { slackMessage -> entityMapper.mapToDomain(slackMessage) }
        }
    }

    override fun streamLocalMessages(
        workspaceId: String,
        userId: String,
        limit: Int,
        offset: Int
    ): Flow<List<DomainLayerMessages.SKMessage>> {
        return slackMessageDao.slackDBQueries.selectAllMessagesByChannelIdPaginated(
            workspaceId,
            userId,
            limit.toLong(),
            offset.toLong()
        )
            .asFlow()
            .flowOn(coroutineMainDispatcherProvider.io)
            .mapToList(coroutineMainDispatcherProvider.default)
            .map { slackMessages ->
                slackMessages
                    .map { slackMessage -> entityMapper.mapToDomain(slackMessage) }
            }
            .flowOn(coroutineMainDispatcherProvider.default)
    }

    override suspend fun saveMessage(
        params: DomainLayerMessages.SKMessage,
    ): DomainLayerMessages.SKMessage {
        return withContext(coroutineMainDispatcherProvider.io) {
            slackMessageDao.slackDBQueries.insertMessage(
                params.uuid,
                params.workspaceId,
                params.channelId,
                params.messageFirst,
                params.messageSecond,
                params.sender,
                params.createdDate,
                params.modifiedDate,
                if (params.isDeleted) 1 else 0,
                if (params.isSynced) 1 else 0,
            )
            params
        }
    }
}
