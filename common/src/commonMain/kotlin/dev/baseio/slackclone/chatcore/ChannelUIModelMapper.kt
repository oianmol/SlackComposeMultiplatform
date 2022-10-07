package dev.baseio.slackclone.chatcore

import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackdomain.mappers.UiModelMapper
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

class ChannelUIModelMapper :
  UiModelMapper<DomainLayerChannels.SKChannel, UiLayerChannels.SKChannel> {
  override fun mapToPresentation(model: DomainLayerChannels.SKChannel): UiLayerChannels.SKChannel {
    return UiLayerChannels.SKChannel(
      model.name,
      model.workspaceId,
      model.isPrivate,
      model.uuid!!,
      model.createdDate,
      model.modifiedDate,
      model.isStarred,
      model.isShareOutSide,
      model.isMuted,
      model.isOneToOne,
      model.avatarUrl
    )
  }

  override fun mapToDomain(modelItem: UiLayerChannels.SKChannel): DomainLayerChannels.SKChannel {
    return DomainLayerChannels.SKChannel(
      modelItem.uuid,
      modelItem.workspaceId,
      modelItem.name,
      modelItem.createdDate,
      modelItem.modifiedDate,
      modelItem.isMuted,
      modelItem.isPrivate,
      modelItem.isStarred,
      modelItem.isShareOutside,
      modelItem.isOneToOne,
      modelItem.pictureUrl

    )
  }
}