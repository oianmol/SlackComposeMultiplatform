package dev.baseio.slackdata.datasources.local.workspaces

import dev.baseio.database.SlackDB
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.local.workspaces.SKLocalDataSourceWriteWorkspaces
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import kotlinx.coroutines.withContext

class SKLocalDataSourceWriteWorkspacesImpl(
  private val slackDB: SlackDB,
  private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : SKLocalDataSourceWriteWorkspaces {
  override suspend fun saveWorkspaces(list: List<DomainLayerWorkspaces.SKWorkspace>) {
    return withContext(coroutineDispatcherProvider.io) {
      slackDB.transaction {
        list.map { skWorkspace ->
          slackDB.slackDBQueries.insertWorkspace(
            skWorkspace.uuid,
            skWorkspace.uuid + skWorkspace.token,
            skWorkspace.name,
            skWorkspace.domain,
            skWorkspace.picUrl,
            skWorkspace.modifiedTime,
            skWorkspace.token
          )
        }
      }

    }
  }
}