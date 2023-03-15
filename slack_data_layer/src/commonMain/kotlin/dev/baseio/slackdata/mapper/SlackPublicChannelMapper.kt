package dev.baseio.slackdata.mapper

import database.SkPublicChannel
import dev.baseio.slackdata.datasources.remote.channels.toByteArray
import dev.baseio.slackdata.datasources.remote.channels.toSKUserPublicKey
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

class SlackPublicChannelMapper :
  EntityMapper<DomainLayerChannels.SKChannel, SkPublicChannel> {
  override fun mapToDomain(entity: SkPublicChannel): DomainLayerChannels.SKChannel {
    return DomainLayerChannels.SKChannel.SkGroupChannel(
      uuid = entity.uuid,
      name = entity.name,
      createdDate = entity.createdDate,
      modifiedDate = entity.modifiedDate,
      avatarUrl = entity.photo,
      workId = entity.workspaceId,
      deleted = entity.isDeleted == 1L,
      channelPublicKey = entity.publicKey.toSKUserPublicKey()
    )
  }

  override fun mapToData(model: DomainLayerChannels.SKChannel): SkPublicChannel {
    model as DomainLayerChannels.SKChannel.SkGroupChannel
    return SkPublicChannel(
      uuid = model.uuid,
      name = model.name,
      createdDate = model.createdDate,
      modifiedDate = model.modifiedDate,
      photo = model.avatarUrl,
      workspaceId = model.workId,
      isDeleted = if (model.deleted) 1 else 0,
      publicKey = model.publicKey.toByteArray()
    )
  }
}