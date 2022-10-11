package dev.baseio.slackdata.mapper

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.datetime.Clock

class SlackUserToChannelMapper : EntityToMapper<DomainLayerUsers.SKUser, DomainLayerChannels.SKChannel> {
  override fun mapToDomain1(entity: DomainLayerChannels.SKChannel): DomainLayerUsers.SKUser {
    TODO("Not yet implemented")
  }

  override fun mapToDomain2(model: DomainLayerUsers.SKUser): DomainLayerChannels.SKChannel {
    return DomainLayerChannels.SKChannel(
      model.uuid,
      model.workspaceId,
      name = model.name,
      createdDate = Clock.System.now().toEpochMilliseconds(),
      modifiedDate = Clock.System.now().toEpochMilliseconds(),
      avatarUrl = model.avatarUrl,
      isPrivate = true,
      isShareOutSide = false,
      isMuted = false,
      isStarred = false,
      isOneToOne = true
    )
  }
}