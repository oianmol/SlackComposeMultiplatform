package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceChannelMembers
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceReadChannelMembers
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UseCaseGetChannelMembers(
    private val skNetworkDataSourceReadChannelMembers: SKNetworkDataSourceReadChannelMembers,
    private val localSource: SKLocalDataSourceChannelMembers,
    private val skLocalDataSourceUsers: SKLocalDataSourceUsers
) {
    operator fun invoke(useCaseWorkspaceChannelRequest: UseCaseWorkspaceChannelRequest): Flow<List<DomainLayerUsers.SKUser>> {
        return flow {
            //fetch from network
            val result = skNetworkDataSourceReadChannelMembers.fetchChannelMembers(
                request = useCaseWorkspaceChannelRequest
            )
            //save to local
            localSource.save(members = result.getOrDefault(emptyList()))
            // get from local
            localSource.get(
                workspaceId = useCaseWorkspaceChannelRequest.workspaceId,
                channelId = useCaseWorkspaceChannelRequest.channelId!!
            )
                .map { skChannelMembers ->
                    skChannelMembers.mapNotNull {
                        skLocalDataSourceUsers.getUser(
                            workspaceId = useCaseWorkspaceChannelRequest.workspaceId,
                            uuid = it.memberId
                        )
                    }
                }
        }
    }
}
