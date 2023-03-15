package dev.baseio.slackdomain.datasources.remote.workspaces

import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces

interface SKNetworkDataSourceReadWorkspaces {
  suspend fun getWorkspaces(token: String): List<DomainLayerWorkspaces.SKWorkspace>
}