package dev.baseio.slackserver.data.sources

import dev.baseio.slackserver.data.models.SkWorkspace
import kotlinx.coroutines.flow.Flow

interface WorkspaceDataSource {
    suspend fun getWorkspaces(): List<SkWorkspace>
    suspend fun saveWorkspace(skWorkspace: SkWorkspace): SkWorkspace?
    suspend fun getWorkspace(workspaceId: String): SkWorkspace?
    suspend fun findWorkspacesForEmail(email: String): List<SkWorkspace>
    suspend fun findWorkspaceForName(name: String): SkWorkspace?
    suspend fun updateWorkspace(toDBWorkspace: SkWorkspace): SkWorkspace?
    fun registerForChanges(uuid: String?): Flow<Pair<SkWorkspace?, SkWorkspace?>>
}


