package dev.baseio.slackclone.data.repository

import database.SlackChannel
import dev.baseio.database.SlackDB
import dev.baseio.slackclone.common.injection.dispatcher.CoroutineDispatcherProvider
import dev.baseio.slackclone.data.local.asFlow
import dev.baseio.slackclone.data.local.mapToList
import dev.baseio.slackclone.data.mapper.EntityMapper
import dev.baseio.slackclone.domain.model.channel.DomainLayerChannels
import dev.baseio.slackclone.domain.model.users.DomainLayerUsers
import dev.baseio.slackclone.domain.repository.ChannelsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class SlackChannelsRepositoryImpl(
  private val slackChannelDao: SlackDB,
  private val slackChannelMapper: EntityMapper<DomainLayerChannels.SlackChannel, SlackChannel>,
  private val coroutineMainDispatcherProvider: CoroutineDispatcherProvider
) :
  ChannelsRepository {

  override fun fetchChannelsPaged(params: String?): Flow<List<DomainLayerChannels.SlackChannel>> {
    val flow = params?.takeIf { it.isNotEmpty() }?.let {
      slackChannelDao.slackDBQueries.selectAllChannelsByName(params).asFlow().mapToList(coroutineMainDispatcherProvider.default)
    } ?: run {
      slackChannelDao.slackDBQueries.selectAllChannels().asFlow().mapToList(coroutineMainDispatcherProvider.default)
    }
    return flow.map {
      it.map { message ->
        slackChannelMapper.mapToDomain(message)
      }
    }
  }

  override suspend fun channelCount(): Long {
    return slackChannelDao.slackDBQueries.countChannels().executeAsOne()
  }

  override fun fetchChannels(): Flow<List<DomainLayerChannels.SlackChannel>> {
    return slackChannelDao.slackDBQueries.selectAllChannels().asFlow().mapToList(coroutineMainDispatcherProvider.default)
      .map { list -> dbToDomList(list) }
  }

  private fun dbToDomList(list: List<SlackChannel>) =
    list.map { channel -> slackChannelMapper.mapToDomain(channel) }

  override suspend fun getChannel(uuid: String): DomainLayerChannels.SlackChannel {
    val dbSlack = slackChannelDao.slackDBQueries.selectChannelById(uuid).executeAsOne()
    return slackChannelMapper.mapToDomain(dbSlack)
  }

  override suspend fun saveOneToOneChannels(params: List<DomainLayerUsers.SlackUser>) {
    return withContext(coroutineMainDispatcherProvider.io) {
      params.forEach {
        slackChannelDao.slackDBQueries.insertChannel(
          it.login,
          it.name,
          it.email,
           Clock.System.now().toEpochMilliseconds(),  Clock.System.now().toEpochMilliseconds(), 0L, 0L, 1L, 0L, it.picture, 1L
        )
      }
    }
  }

  override suspend fun saveChannel(params: DomainLayerChannels.SlackChannel): DomainLayerChannels.SlackChannel? {
    return withContext(coroutineMainDispatcherProvider.io) {
      slackChannelDao.slackDBQueries.insertChannel(
        params.uuid!!,
        params.name,
        "someelamil@sdffd.com",
        params.createdDate,
        params.modifiedDate,
        params.isMuted.let { if (it == true) 1L else 0L },
        params.isStarred.let { if (it == true) 1L else 0L },
        params.isPrivate.let { if (it == true) 1L else 0L },
        params.isShareOutSide.let { if (it == true) 1L else 0L },
        params.avatarUrl,
        params.isOneToOne.let { if (it == true) 1L else 0L }
      )
      slackChannelDao.slackDBQueries.selectChannelById(params.uuid).executeAsOne().let { slackChannelMapper.mapToDomain(it) }
    }
  }
}
