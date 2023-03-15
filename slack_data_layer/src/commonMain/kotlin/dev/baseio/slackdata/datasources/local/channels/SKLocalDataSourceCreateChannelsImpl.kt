package dev.baseio.slackdata.datasources.local.channels

import database.SkDMChannel
import database.SkPublicChannel
import dev.baseio.database.SlackDB
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdata.mapper.EntityMapper
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceCreateChannels
import kotlinx.coroutines.withContext

class SKLocalDataSourceCreateChannelsImpl(
  private val slackChannelDao: SlackDB,
  private val dmChannelMapper: EntityMapper<DomainLayerChannels.SKChannel, SkDMChannel>,
  private val publicChannelMapper: EntityMapper<DomainLayerChannels.SKChannel, SkPublicChannel>,
  private val coroutineMainDispatcherProvider: CoroutineDispatcherProvider
) :
  SKLocalDataSourceCreateChannels {

  override suspend fun saveChannel(params: DomainLayerChannels.SKChannel): DomainLayerChannels.SKChannel {
    return withContext(coroutineMainDispatcherProvider.io) {
      when (params) {
        is DomainLayerChannels.SKChannel.SkGroupChannel -> {
          slackChannelDao.slackDBQueries.insertPublicChannel(
            uuid = params.uuid,
            workspaceId = params.workId,
            name = params.name,
            createdDate = params.createdDate,
            modifiedDate = params.modifiedDate,
            isDeleted = if (params.deleted) 1 else 0,
            photo = params.avatarUrl,
            publicKey = params.channelPublicKey.keyBytes
          )
          slackChannelDao.slackDBQueries.selectPublicChannelById(params.workId, params.uuid).executeAsOne()
            .let { publicChannelMapper.mapToDomain(it) }
        }

        is DomainLayerChannels.SKChannel.SkDMChannel -> {
          slackChannelDao.slackDBQueries.insertDMChannel(
            uuid = params.uuid,
            workspaceId = params.workId,
            createdDate = params.createdDate,
            modifiedDate = params.modifiedDate,
            senderId = params.senderId,
            receiverId = params.receiverId,
            isDeleted = if (params.deleted) 1 else 0,
            publicKey = params.channelPublicKey.keyBytes
          )
          slackChannelDao.slackDBQueries.selectDMChannelById(params.workId, params.uuid).executeAsOne()
            .let { dmChannelMapper.mapToDomain(it) }
        }
      }


    }
  }
}
