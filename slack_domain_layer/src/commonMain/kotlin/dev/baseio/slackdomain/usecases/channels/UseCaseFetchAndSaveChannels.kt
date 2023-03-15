package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceCreateChannels
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceReadChannels

class UseCaseFetchAndSaveChannels(
    private val skNetworkDataSourceReadChannels: SKNetworkDataSourceReadChannels,
    private val skLocalDataSourceWriteChannels: SKLocalDataSourceCreateChannels,
) {
    suspend operator fun invoke(
        workspaceId: String,
        offset: Int,
        limit: Int
    ) {
        kotlin.runCatching {
            val channels = skNetworkDataSourceReadChannels
                .fetchChannels(workspaceId = workspaceId, offset, limit).getOrThrow()
            channels.map { skChannel ->
                skLocalDataSourceWriteChannels.saveChannel(skChannel)
            }
        }.run {
            when {
                isFailure -> {
                    // TODO update upstream of errors!
                }
            }
        }

    }

}
