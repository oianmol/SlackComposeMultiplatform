package dev.baseio.slackdata.mapper

import database.SlackChannel
import dev.baseio.slackdomain.model.users.DomainLayerUsers

class SlackUserChannelMapper :
  EntityMapper<DomainLayerUsers.SKUser, SlackChannel> {
  override fun mapToDomain(entity: SlackChannel): DomainLayerUsers.SKUser {
    TODO("Not yet implemented")
  }

  override fun mapToData(model: DomainLayerUsers.SKUser): SlackChannel {
    return SlackChannel(
      model.username + model.workspaceId,
      model.workspaceId,
      model.name,
      isStarred = 0L,
      photo = model.avatarUrl,
      createdDate = null,
      modifiedDate = null,
      isMuted = 0L,
      isPrivate = 1L,
      isShareOutSide = null,
      isOneToOne = 1L
    )
  }
}