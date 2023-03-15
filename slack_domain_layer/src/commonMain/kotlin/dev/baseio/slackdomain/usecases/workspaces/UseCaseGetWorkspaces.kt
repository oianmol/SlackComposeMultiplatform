package dev.baseio.slackdomain.usecases.workspaces

import dev.baseio.slackdomain.datasources.local.workspaces.SKLocalDataSourceReadWorkspaces
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import kotlinx.coroutines.flow.Flow

class UseCaseGetWorkspaces(
    private val readWorkspacesSource: SKLocalDataSourceReadWorkspaces,
) {
    operator fun invoke(): Flow<List<DomainLayerWorkspaces.SKWorkspace>> {
        return readWorkspacesSource.fetchWorkspaces()
    }
}