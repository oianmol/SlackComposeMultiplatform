package dev.baseio.slackclone.chatcore.injection

import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackdomain.mappers.UiModelMapper
import dev.baseio.slackdomain.model.users.DomainLayerUsers

class UserChannelUiMapper constructor():
  UiModelMapper<DomainLayerUsers.SKUser, UiLayerChannels.SKChannel> {
  override fun mapToPresentation(model: DomainLayerUsers.SKUser): UiLayerChannels.SKChannel {
    TODO("Not yet implemented")
  }

  override fun mapToDomain(modelItem: UiLayerChannels.SKChannel): DomainLayerUsers.SKUser {
    TODO("Not yet implemented")
  }
}