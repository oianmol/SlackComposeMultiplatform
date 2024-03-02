package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels

class UseCaseFetchChannelCount(private val skLocalDataSourceReadChannels: SKLocalDataSourceReadChannels) {
    suspend operator fun invoke(workspaceId: String): Int {
        return skLocalDataSourceReadChannels.channelCount(workspaceId).toInt()
    }
}
