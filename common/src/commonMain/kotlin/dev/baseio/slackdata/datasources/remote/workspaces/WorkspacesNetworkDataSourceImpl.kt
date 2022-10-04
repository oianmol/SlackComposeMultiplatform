package dev.baseio.slackdata.datasources.remote.workspaces

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdata.protos.KMSKWorkspace
import dev.baseio.slackdata.protos.KMSKWorkspaces
import dev.baseio.slackdomain.datasources.remote.workspaces.WorkspacesNetworkDataSource
import dev.baseio.slackdomain.usecases.workspaces.Email
import dev.baseio.slackdomain.usecases.workspaces.Name

class WorkspacesNetworkDataSourceImpl(private val grpcCalls: GrpcCalls) : WorkspacesNetworkDataSource {
  override suspend fun findWorkspacesForEmail(email: Email): KMSKWorkspaces {
    return grpcCalls.findWorkspacesForEmail(email)
  }

  override suspend fun findWorkspaceByName(name: Name): KMSKWorkspace {
    return grpcCalls.findWorkspaceByName(name)
  }
}