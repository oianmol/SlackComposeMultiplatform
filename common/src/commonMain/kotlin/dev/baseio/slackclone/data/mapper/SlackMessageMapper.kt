package dev.baseio.slackclone.data.mapper

import database.SlackMessage
import dev.baseio.slackclone.domain.model.message.DomainLayerMessages
class SlackMessageMapper constructor() : EntityMapper<DomainLayerMessages.SlackMessage, SlackMessage> {
  override fun mapToDomain(entity: SlackMessage): DomainLayerMessages.SlackMessage {
    return DomainLayerMessages.SlackMessage(
      entity.uid,
      entity.channelId!!,
      entity.message!!,
      entity.uid,
      entity.createdBy!!,
      entity.createdDate!!,
      entity.modifiedDate!!
    )
  }

  override fun mapToData(model: DomainLayerMessages.SlackMessage): SlackMessage {
    return SlackMessage(
      model.uuid,
      model.channelId,
      model.message,
      model.userId,
      model.createdBy,
      model.createdDate,
      model.modifiedDate,
    )
  }
}