package dev.baseio.slackclone.data.mapper

import database.SlackChannel
import dev.baseio.slackclone.domain.model.users.DomainLayerUsers

class SlackUserChannelMapper :
  EntityMapper<DomainLayerUsers.SlackUser, SlackChannel> {
  override fun mapToDomain(entity: SlackChannel): DomainLayerUsers.SlackUser {
    TODO("Not yet implemented")
  }

  override fun mapToData(model: DomainLayerUsers.SlackUser): SlackChannel {
    return SlackChannel(
      model.login,
      model.name,
      isStarred = 0L,
      photo = model.picture,
      email = "",
      createdDate = null,
      modifiedDate = null,
      isMuted = 0L,
      isPrivate = 1L,
      isShareOutSide = null,
      isOneToOne = 1L
    )
  }
}