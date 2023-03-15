package dev.baseio.slackdata.datasources.remote.users

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackdata.protos.KMSKUser
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.remote.users.SKNetworkDataSourceReadUsers
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SKNetworkDataSourceReadUsersImpl(
    private val grpcCalls: IGrpcCalls,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : SKNetworkDataSourceReadUsers {
    override suspend fun fetchUsers(workspaceId: String): Result<List<DomainLayerUsers.SKUser>> {
        return withContext(coroutineDispatcherProvider.io) {
            kotlin.runCatching {
                val users = grpcCalls.getUsersForWorkspaceId(workspaceId)
                users.usersList.map { kmskUser ->
                    kmskUser.skUser()
                }
            }
        }
    }

    fun KMSKUser.skUser() = DomainLayerUsers.SKUser(
        this.uuid,
        this.workspaceId,
        this.gender,
        this.name,
        this.location,
        this.email,
        this.username,
        this.userSince,
        this.phone,
        this.avatarUrl,
        DomainLayerUsers.SKSlackKey(this.publicKey.keybytesList.map { it.byte.toByte() }.toByteArray())
    )

    override fun listenToChangeInUsers(workspaceId: String): Flow<Pair<DomainLayerUsers.SKUser?, DomainLayerUsers.SKUser?>> {
        return grpcCalls.listenToChangeInUsers(workspaceId = workspaceId).map { message ->
            Pair(
                if (message.hasPrevious()) message.previous.skUser() else null,
                if (message.hasLatest()) message.latest.skUser() else null
            )
        }.catch {
            // notify upstream for these errors
        }
    }
}