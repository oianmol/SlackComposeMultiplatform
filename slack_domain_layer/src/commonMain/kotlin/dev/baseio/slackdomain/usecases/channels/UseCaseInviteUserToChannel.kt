package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkSourceChannel
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

class UseCaseInviteUserToChannel(private val networkSourceChannels: SKNetworkSourceChannel) {

  suspend fun inviteUserToChannelFromOtherDeviceOrUser(channel: DomainLayerChannels.SKChannel, userName: String): Result<List<DomainLayerChannels.SkChannelMember>> {
    return kotlin.runCatching {
      networkSourceChannels.inviteUserToChannelFromOtherDeviceOrUser(channel,userName)
    }.also {
      it.exceptionOrNull()?.printStackTrace()
    }
  }

}