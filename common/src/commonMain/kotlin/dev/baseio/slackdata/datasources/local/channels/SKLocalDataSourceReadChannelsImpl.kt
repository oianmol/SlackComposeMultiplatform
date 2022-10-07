package dev.baseio.slackdata.datasources.local.channels

import database.SlackChannel
import dev.baseio.database.SlackDB
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdata.local.asFlow
import dev.baseio.slackdata.local.mapToList
import dev.baseio.slackdata.mapper.EntityMapper
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SKLocalDataSourceReadChannelsImpl(
  private val slackChannelDao: SlackDB,
  private val SKChannelMapper: EntityMapper<DomainLayerChannels.SKChannel, SlackChannel>,
  private val coroutineMainDispatcherProvider: CoroutineDispatcherProvider
) : SKLocalDataSourceReadChannels {

  override fun fetchChannelsOrByName(workspaceId: String, params: String?): Flow<List<DomainLayerChannels.SKChannel>> {
    val flow = params?.takeIf { it.isNotEmpty() }?.let {
      slackChannelDao.slackDBQueries.selectAllChannelsByName(workspaceId, params).asFlow()
        .mapToList(coroutineMainDispatcherProvider.default)
    } ?: run {
      slackChannelDao.slackDBQueries.selectAllChannels(workspaceId).asFlow()
        .mapToList(coroutineMainDispatcherProvider.default)
    }
    return flow.map {
      it.map { message ->
        SKChannelMapper.mapToDomain(message)
      }
    }
  }

  override fun getChannelById(workspaceId: String,uuid: String): DomainLayerChannels.SKChannel? {
    return slackChannelDao.slackDBQueries.selectChannelById(workspaceId,uuid).executeAsOneOrNull()?.let { SKChannelMapper.mapToDomain(it) }
  }

  override suspend fun channelCount(workspaceId: String): Long {
    return slackChannelDao.slackDBQueries.countChannels(workspaceId).executeAsOne()
  }

  override fun fetchChannels(workspaceId: String): Flow<List<DomainLayerChannels.SKChannel>> {
    return slackChannelDao.slackDBQueries.selectAllChannels(workspaceId).asFlow()
      .mapToList(coroutineMainDispatcherProvider.default)
      .map { list -> dbToDomList(list) }
  }

  private fun dbToDomList(list: List<SlackChannel>) =
    list.map { channel -> SKChannelMapper.mapToDomain(channel) }

  override suspend fun getChannel(request: UseCaseChannelRequest): DomainLayerChannels.SKChannel {
    val dbSlack = slackChannelDao.slackDBQueries.selectChannelById(request.workspaceId, request.uuid).executeAsOne()
    return SKChannelMapper.mapToDomain(dbSlack)
  }


}