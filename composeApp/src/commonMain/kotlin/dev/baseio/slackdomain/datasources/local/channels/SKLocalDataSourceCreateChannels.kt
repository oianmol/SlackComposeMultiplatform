package dev.baseio.slackdomain.datasources.local.channels

import dev.baseio.slackdomain.model.channel.DomainLayerChannels

interface SKLocalDataSourceCreateChannels {
    suspend fun saveChannel(params: DomainLayerChannels.SKChannel): DomainLayerChannels.SKChannel
}
