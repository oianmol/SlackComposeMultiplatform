package dev.baseio.slackdomain.usecases.workspaces

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdata.protos.KMSKWorkspace
import dev.baseio.slackdata.protos.KMSKWorkspaces
import dev.baseio.slackdomain.datasources.remote.workspaces.WorkspacesNetworkDataSource

typealias Email = String
typealias Name = String

class FindWorkspacesUseCase(private val workspacesNetworkDataSource: WorkspacesNetworkDataSource) {
  suspend fun byEmail(email: Email): KMSKWorkspaces {
    return workspacesNetworkDataSource.findWorkspacesForEmail(email)
  }

  suspend fun byName(name: Name): KMSKWorkspace {
    return workspacesNetworkDataSource.findWorkspaceByName(name)
  }
}