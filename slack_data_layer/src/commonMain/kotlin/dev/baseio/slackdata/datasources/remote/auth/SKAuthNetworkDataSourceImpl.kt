package dev.baseio.slackdata.datasources.remote.auth

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackdata.datasources.remote.channels.toUserPublicKey
import dev.baseio.slackdomain.datasources.remote.auth.SKAuthNetworkDataSource
import dev.baseio.slackdomain.model.users.DomainLayerUsers

class SKAuthNetworkDataSourceImpl(private val grpcCalls: IGrpcCalls) : SKAuthNetworkDataSource {
    override suspend fun getLoggedInUser(): Result<DomainLayerUsers.SKUser> {
        return kotlin.runCatching {
            val result = grpcCalls.currentLoggedInUser()
            DomainLayerUsers.SKUser(
                result.uuid,
                result.workspaceId,
                result.gender,
                result.name,
                result.location,
                result.email,
                result.username,
                result.userSince,
                result.phone,
                result.avatarUrl,
                result.publicKey.toUserPublicKey()
            )
        }
    }
}