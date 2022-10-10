package dev.baseio.slackdata.datasources.local.channels

import database.SlackChannel
import database.SlackMessage
import dev.baseio.database.SlackDB
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdata.local.asFlow
import dev.baseio.slackdata.local.mapToList
import dev.baseio.slackdata.mapper.EntityMapper
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.datasources.local.channels.SKDataSourceChannelLastMessage
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SlackSKDataSourceChannelLastMessage constructor(
  private val slackChannelDao: SlackDB,
  private val messagesMapper: EntityMapper<DomainLayerMessages.SKMessage, SlackMessage>,
  private val SKChannelMapper: EntityMapper<DomainLayerChannels.SKChannel, SlackChannel>,
  private val skLocalDataSourceUsers: SKLocalDataSourceUsers,
  private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : SKDataSourceChannelLastMessage {
  override fun fetchChannelsWithLastMessage(workspaceId: String): Flow<List<DomainLayerMessages.SKLastMessage>> {
    val chatPager = slackChannelDao.slackDBQueries.selectLastMessageOfChannels(workspaceId)
      .asFlow()
      .mapToList(coroutineDispatcherProvider.default)
    return chatPager.map {
      it.map { channelsWithLastMessage ->
        val channel =
          slackChannelDao.slackDBQueries.selectChannelById(workspaceId, channelsWithLastMessage.channelId!!)
            .executeAsOne()
        val message =
          SlackMessage(
            channelsWithLastMessage.uuid,
            channelsWithLastMessage.workspaceId,
            channelsWithLastMessage.channelId,
            channelsWithLastMessage.message,
            channelsWithLastMessage.receiver_,
            channelsWithLastMessage.sender,
            channelsWithLastMessage.createdDate,
            channelsWithLastMessage.modifiedDate,
          )
        DomainLayerMessages.SKLastMessage(
          SKChannelMapper.mapToDomain(channel),
          messagesMapper.mapToDomain(message)
        )
      }
    }
  }
}