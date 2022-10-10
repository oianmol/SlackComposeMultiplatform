package dev.baseio.slackdomain.model.message

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.users.DomainLayerUsers


interface DomainLayerMessages {
  data class SKMessage(
    val uuid: String,
    val workspaceId: String,
    val channelId: String,
    val message: String,
    val receiver: String,
    val sender: String,
    val createdDate: Long,
    val modifiedDate: Long,
    var senderInfo: DomainLayerUsers.SKUser? = null
  )

  data class SKLastMessage(
    val channel: DomainLayerChannels.SKChannel,
    val message: SKMessage
  )
}