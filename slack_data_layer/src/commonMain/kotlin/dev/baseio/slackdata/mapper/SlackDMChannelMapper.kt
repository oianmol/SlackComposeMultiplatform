package dev.baseio.slackdata.mapper

import database.SkDMChannel
import dev.baseio.slackdata.datasources.remote.channels.toByteArray
import dev.baseio.slackdata.datasources.remote.channels.toSKUserPublicKey
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

class SlackDMChannelMapper : EntityMapper<DomainLayerChannels.SKChannel, SkDMChannel> {
  override fun mapToDomain(entity: SkDMChannel): DomainLayerChannels.SKChannel.SkDMChannel {
    return DomainLayerChannels.SKChannel.SkDMChannel(
      uuid = entity.uuid,
      createdDate = entity.createdDate,
      modifiedDate = entity.modifiedDate,
      workId = entity.workspaceId,
      deleted = entity.isDeleted == 1L,
      senderId = entity.senderId,
      receiverId = entity.receiverId,
      channelPublicKey = entity.publicKey.toSKUserPublicKey()
    )
  }

  override fun mapToData(model: DomainLayerChannels.SKChannel): SkDMChannel {
    model as DomainLayerChannels.SKChannel.SkDMChannel
    return SkDMChannel(
      uuid = model.uuid,
      createdDate = model.createdDate,
      modifiedDate = model.modifiedDate,
      workspaceId = model.workId,
      isDeleted = if (model.deleted) 1 else 0,
      senderId = model.senderId,
      receiverId = model.receiverId,
      publicKey = model.publicKey.toByteArray()
    )
  }
}