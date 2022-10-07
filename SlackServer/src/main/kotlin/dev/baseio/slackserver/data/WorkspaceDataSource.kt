package dev.baseio.slackserver.data

import com.squareup.sqldelight.Query
import database.FindWorkspacesForEmailId
import database.SkWorkspace
import dev.baseio.slackdata.protos.SKWorkspace
import dev.baseio.slackdata.protos.SKWorkspaces
import kotlinx.coroutines.flow.Flow

interface WorkspaceDataSource {
  fun getWorkspaces(): Flow<Query<SkWorkspace>>
  fun saveWorkspace(skWorkspace: SkWorkspace) :SkWorkspace
  fun findWorkspacesForEmail(email: String): List<FindWorkspacesForEmailId>
  fun findWorkspaceForName(name: String): SkWorkspace?
}