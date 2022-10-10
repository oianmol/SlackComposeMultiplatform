package dev.baseio.slackdata.datasources.remote.workspaces

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdata.protos.KMSKWorkspace
import dev.baseio.slackdata.protos.KMSKWorkspaces
import dev.baseio.slackdomain.datasources.remote.workspaces.SKNetworkDataSourceReadWorkspaces
import dev.baseio.slackdomain.usecases.workspaces.Email
import dev.baseio.slackdomain.usecases.workspaces.Name
import kotlinx.coroutines.flow.Flow

class SKNetworkDataSourceReadWorkspacesImpl(private val grpcCalls: GrpcCalls) : SKNetworkDataSourceReadWorkspaces {
  override suspend fun findWorkspacesForEmail(email: Email): KMSKWorkspaces {
    return grpcCalls.findWorkspacesForEmail(email)
  }

  override suspend fun findWorkspaceByName(name: Name): KMSKWorkspace {
    return grpcCalls.findWorkspaceByName(name)
  }

  override fun getWorkspaces(): Flow<KMSKWorkspaces> {
    return grpcCalls.getWorkspaces()
  }
}