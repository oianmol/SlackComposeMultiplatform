package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceCreateChannels
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceWriteChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

class UseCaseCreateChannel(
    private val SKLocalDataSourceCreateChannels: SKLocalDataSourceCreateChannels,
    private val skNetworkDataSourceWriteChannels: SKNetworkDataSourceWriteChannels,
    private val skLocalDataSourceReadChannels: SKLocalDataSourceReadChannels
) {
    suspend operator fun invoke(params: DomainLayerChannels.SKChannel): Result<DomainLayerChannels.SKChannel> {
        return kotlin.runCatching {
            val channel = skNetworkDataSourceWriteChannels.createChannel(params).getOrThrow()
            SKLocalDataSourceCreateChannels.saveChannel(channel)
            skLocalDataSourceReadChannels.getChannelById(channel.workspaceId, channel.channelId)!!
        }
    }
}
