package dev.baseio.slackdomain.usecases.workspaces

import dev.baseio.slackdata.protos.KMSKWorkspace
import dev.baseio.slackdata.protos.KMSKWorkspaces
import dev.baseio.slackdomain.datasources.remote.workspaces.SKNetworkDataSourceReadWorkspaces

typealias Email = String
typealias Name = String

class FindWorkspacesUseCase(private val SKNetworkDataSourceReadWorkspaces: SKNetworkDataSourceReadWorkspaces) {
  suspend fun byEmail(email: Email): KMSKWorkspaces {
    return SKNetworkDataSourceReadWorkspaces.findWorkspacesForEmail(email)
  }

  suspend fun byName(name: Name): KMSKWorkspace {
    return SKNetworkDataSourceReadWorkspaces.findWorkspaceByName(name)
  }
}