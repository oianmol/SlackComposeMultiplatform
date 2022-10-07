package dev.baseio.slackdata.datasources.local.channels

import database.SlackChannel
import dev.baseio.database.SlackDB
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdata.mapper.EntityMapper
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceCreateChannels
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class SKLocalDataSourceCreateChannelsImpl(
  private val slackChannelDao: SlackDB,
  private val SKChannelMapper: EntityMapper<DomainLayerChannels.SKChannel, SlackChannel>,
  private val coroutineMainDispatcherProvider: CoroutineDispatcherProvider
) :
  SKLocalDataSourceCreateChannels {

  override suspend fun saveChannels(channels: MutableList<DomainLayerChannels.SKChannel>) {
    channels.forEach { skChannel ->
      saveChannel(skChannel)
    }
  }

  override suspend fun saveOneToOneChannels(params: List<DomainLayerUsers.SKUser>) {
    return withContext(coroutineMainDispatcherProvider.io) {
      params.forEach {
        slackChannelDao.slackDBQueries.insertChannel(
          it.username,
          it.workspaceId,
          it.name,
          Clock.System.now().toEpochMilliseconds(),
          Clock.System.now().toEpochMilliseconds(),
          0L,
          0L,
          1L,
          0L,
          it.avatarUrl,
          1L
        )
      }
    }
  }

  override suspend fun saveChannel(params: DomainLayerChannels.SKChannel): DomainLayerChannels.SKChannel {
    return withContext(coroutineMainDispatcherProvider.io) {
      slackChannelDao.slackDBQueries.insertChannel(
        params.uuid!!,
        params.workspaceId,
        params.name,
        params.createdDate,
        params.modifiedDate,
        params.isMuted.let { if (it == true) 1L else 0L },
        params.isStarred.let { if (it == true) 1L else 0L },
        params.isPrivate.let { if (it == true) 1L else 0L },
        params.isShareOutSide.let { if (it == true) 1L else 0L },
        params.avatarUrl,
        params.isOneToOne.let { if (it == true) 1L else 0L }
      )
      slackChannelDao.slackDBQueries.selectChannelById(params.workspaceId, params.uuid).executeAsOne()
        .let { SKChannelMapper.mapToDomain(it) }
    }
  }
}
