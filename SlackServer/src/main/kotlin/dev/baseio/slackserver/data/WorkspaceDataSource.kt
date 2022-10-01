package dev.baseio.slackserver.data

import com.squareup.sqldelight.Query
import database.SkWorkspace
import kotlinx.coroutines.flow.Flow

interface WorkspaceDataSource {
  fun getWorkspaces(): Flow<Query<SkWorkspace>>
  fun saveWorkspace(skWorkspace: SkWorkspace) :SkWorkspace
}