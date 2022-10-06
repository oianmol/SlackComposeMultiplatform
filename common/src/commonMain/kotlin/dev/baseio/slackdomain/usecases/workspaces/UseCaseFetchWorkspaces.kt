package dev.baseio.slackdomain.usecases.workspaces

import dev.baseio.slackdata.protos.KMSKWorkspace
import dev.baseio.slackdata.protos.KMSKWorkspaces
import dev.baseio.slackdomain.datasources.local.workspaces.SKLocalDataSourceReadWorkspaces
import dev.baseio.slackdomain.datasources.local.workspaces.SKLocalDataSourceWriteWorkspaces
import dev.baseio.slackdomain.datasources.remote.workspaces.SKNetworkDataSourceReadWorkspaces
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest

class UseCaseFetchWorkspaces(
  private val skNetworkDataSourceReadWorkspaces: SKNetworkDataSourceReadWorkspaces,
  private val skLocalDataSourceWriteWorkspaces: SKLocalDataSourceWriteWorkspaces,
  private val skLocalDataSourceReadWorkspaces: SKLocalDataSourceReadWorkspaces
) {
  operator fun invoke(): Flow<List<DomainLayerWorkspaces.SKWorkspace>> {
    return skNetworkDataSourceReadWorkspaces.getWorkspaces().mapLatest { kmSKWorkspaces ->
      val workspaces = kmSKWorkspaces.workspacesList.map {
        it.toSKWorkspace().copy(lastSelected = false)
      }
      skLocalDataSourceWriteWorkspaces.saveWorkspaces(workspaces)
    }.flatMapLatest {
      skLocalDataSourceReadWorkspaces.fetchWorkspaces()
    }
  }
}

private fun KMSKWorkspace.toSKWorkspace(): DomainLayerWorkspaces.SKWorkspace {
  return DomainLayerWorkspaces.SKWorkspace(this.uuid, this.name, this.domain, this.picUrl, this.lastSelected)
}
