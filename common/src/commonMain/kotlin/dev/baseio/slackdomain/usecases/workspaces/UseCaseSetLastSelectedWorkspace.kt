package dev.baseio.slackdomain.usecases.workspaces

import dev.baseio.slackdomain.datasources.local.workspaces.SKLocalDataSourceReadWorkspaces
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces

class UseCaseSetLastSelectedWorkspace(private val skLocalDataSourceReadWorkspaces: SKLocalDataSourceReadWorkspaces) {

    suspend operator fun invoke(skWorkspace: DomainLayerWorkspaces.SKWorkspace){
        return skLocalDataSourceReadWorkspaces.setLastSelected(skWorkspace)
    }
}
