package dev.baseio.slackdomain.datasources.remote.channels

import dev.baseio.slackdomain.model.channel.DomainLayerChannels

interface SKNetworkSourceChannel {
    suspend fun inviteUserToChannelFromOtherDeviceOrUser(
        channel: DomainLayerChannels.SKChannel,
        userName: String
    ): List<DomainLayerChannels.SkChannelMember>
}
