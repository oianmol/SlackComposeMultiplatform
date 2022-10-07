package dev.baseio.slackserver.data.impl

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import database.FindWorkspacesForEmailId
import database.SkWorkspace
import dev.baseio.SlackCloneDB
import dev.baseio.slackdata.protos.SKWorkspace
import dev.baseio.slackdata.protos.SKWorkspaces
import dev.baseio.slackserver.data.WorkspaceDataSource
import kotlinx.coroutines.flow.Flow

class WorkspaceDataSourceImpl(private val slackCloneDB: SlackCloneDB) : WorkspaceDataSource {
  override fun getWorkspaces(): Flow<Query<SkWorkspace>> {
    return slackCloneDB.slackschemaQueries
      .selectAllWorkspaces()
      .asFlow()
  }

  override fun findWorkspacesForEmail(email: String): List<FindWorkspacesForEmailId> {
    return slackCloneDB.slackschemaQueries.findWorkspacesForEmailId(email).executeAsList()
  }

  override fun findWorkspaceForName(name: String): SkWorkspace? {
    return slackCloneDB.slackschemaQueries.findWorkspaceByName(name).executeAsOneOrNull()
  }

  override fun saveWorkspace(skWorkspace: SkWorkspace): SkWorkspace {
    // TODO do checks before saving this!
    slackCloneDB.slackschemaQueries.insertWorkspace(
      skWorkspace.uuid,
      skWorkspace.name.lowercase(),
      skWorkspace.domain.lowercase(),
      skWorkspace.picUrl,
      skWorkspace.lastSelected
    )
    return skWorkspace
  }
}