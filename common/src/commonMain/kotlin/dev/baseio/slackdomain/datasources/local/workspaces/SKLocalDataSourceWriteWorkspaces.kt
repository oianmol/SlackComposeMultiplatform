package dev.baseio.slackdomain.datasources.local.workspaces

import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces

interface SKLocalDataSourceWriteWorkspaces {
  suspend fun saveWorkspaces(list: List<DomainLayerWorkspaces.SKWorkspace>)
}