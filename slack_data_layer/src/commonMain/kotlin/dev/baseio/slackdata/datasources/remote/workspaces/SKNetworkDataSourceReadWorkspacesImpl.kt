package dev.baseio.slackdata.datasources.remote.workspaces

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackdata.protos.KMSKWorkspace
import dev.baseio.slackdomain.datasources.remote.workspaces.SKNetworkDataSourceReadWorkspaces
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces

class SKNetworkDataSourceReadWorkspacesImpl(private val grpcCalls: IGrpcCalls) : SKNetworkDataSourceReadWorkspaces {
  override suspend fun getWorkspaces(token: String): List<DomainLayerWorkspaces.SKWorkspace> {
    return kotlin.run {
      val workspaces = grpcCalls.getWorkspaces(token)
      workspaces.workspacesList.map { kmskWorkspace ->
        kmskWorkspace.skWorkspace(token)
      }
    }
  }
}

fun KMSKWorkspace.skWorkspace(token: String) =
  DomainLayerWorkspaces.SKWorkspace(
    this.uuid,
    this.name,
    this.domain,
    this.picUrl,
    this.modifiedTime,
    token
  )