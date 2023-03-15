package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceChannelMembers
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceReadChannelMembers
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UseCaseGetChannelMembers(
    private val skNetworkDataSourceReadChannelMembers: SKNetworkDataSourceReadChannelMembers,
    private val localSource: SKLocalDataSourceChannelMembers,
    private val skLocalDataSourceUsers: SKLocalDataSourceUsers
) {
    operator fun invoke(useCaseWorkspaceChannelRequest: UseCaseWorkspaceChannelRequest): Flow<List<DomainLayerUsers.SKUser>> {
        return flow {
            val result = skNetworkDataSourceReadChannelMembers.fetchChannelMembers(useCaseWorkspaceChannelRequest)
            emit(result.getOrThrow())
        }.flatMapConcat {
            localSource.save(it)
            localSource.get(useCaseWorkspaceChannelRequest.workspaceId, useCaseWorkspaceChannelRequest.channelId!!)
                .map { skChannelMembers ->
                    skChannelMembers.mapNotNull {
                        skLocalDataSourceUsers.getUser(useCaseWorkspaceChannelRequest.workspaceId, it.memberId)
                    }
                }
        }
    }
}