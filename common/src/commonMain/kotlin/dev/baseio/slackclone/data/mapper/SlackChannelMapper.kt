package dev.baseio.slackclone.data.mapper

import database.SlackChannel
import dev.baseio.slackclone.domain.model.channel.DomainLayerChannels

class SlackChannelMapper constructor() :
  EntityMapper<DomainLayerChannels.SlackChannel, SlackChannel> {
  override fun mapToDomain(entity: SlackChannel): DomainLayerChannels.SlackChannel {
    return DomainLayerChannels.SlackChannel(
      isStarred = entity.isStarred == 1L,
      isPrivate = entity.isPrivate == 1L,
      uuid = entity.uid,
      name = entity.name,
      isMuted = entity.isMuted == 1L,
      createdDate = entity.createdDate,
      modifiedDate = entity.modifiedDate,
      isShareOutSide = entity.isShareOutSide == 1L,
      isOneToOne = entity.isOneToOne == 1L,
      avatarUrl = entity.photo
    )
  }

  override fun mapToData(model: DomainLayerChannels.SlackChannel): SlackChannel {
    return SlackChannel(
      model.uuid ?: model.name!!,
      model.name,
      isStarred = model.isStarred.let { if (it == true) 1L else 0L },
      createdDate = model.createdDate,
      modifiedDate = model.modifiedDate,
      isPrivate = model.isPrivate.let { if (it == true) 1L else 0L },
      isShareOutSide = model.isShareOutSide.let { if (it == true) 1L else 0L },
      isOneToOne = model.isOneToOne.let { if (it == true) 1L else 0L },
      photo = model.avatarUrl, email = "", isMuted = model.isMuted.let { if (it == true) 1L else 0L }
    )
  }
}