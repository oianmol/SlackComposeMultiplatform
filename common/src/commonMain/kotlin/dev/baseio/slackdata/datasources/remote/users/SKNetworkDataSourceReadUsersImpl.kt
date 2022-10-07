package dev.baseio.slackdata.datasources.remote.users

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdata.datasources.remote.messages.toDomainLayerMessage
import dev.baseio.slackdomain.datasources.remote.users.SKNetworkDataSourceReadUsers
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.auth.toSKUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest

class SKNetworkDataSourceReadUsersImpl(private val grpcCalls: GrpcCalls) : SKNetworkDataSourceReadUsers {
  override fun fetchUsers(workspaceId: String): Flow<List<DomainLayerUsers.SKUser>> {
    return grpcCalls.streamUsersForWorkspaceId(workspaceId).mapLatest { user->
      user.usersList.map {  it.toSKUser() }
    }
  }
}